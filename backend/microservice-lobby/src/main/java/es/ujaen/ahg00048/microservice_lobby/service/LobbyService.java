package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.exception.*;
import es.ujaen.ahg00048.microservice_lobby.repository.mongo.BoardRepository;
import es.ujaen.ahg00048.microservice_lobby.repository.redis.LobbyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


@Service
@Validated
public class LobbyService {
    @Autowired
    private RedisLockRegistry _lockRegistry;

    @Autowired
    private LobbyRepository _lobbiesRep;

    @Autowired
    private BoardRepository _boardsRep;


    public static int MAX_BOARDS_PER_USER;
    public static int MAX_USERS_PER_LOBBY;
    public static int MAX_PIECES_PER_BOARDS;

    private final static String LOCK_KEY_BASE = "lobby:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    @Autowired
    public LobbyService(
            @Value("${app.user.max.boards}") int max_boards_per_user,
            @Value("${app.lobby.max.users}") int max_users_per_lobby,
            @Value("${app.lobby.board.max.pieces}") int max_pieces_per_boards) {
        MAX_BOARDS_PER_USER = max_boards_per_user;
        MAX_USERS_PER_LOBBY = max_users_per_lobby;
        MAX_PIECES_PER_BOARDS = max_pieces_per_boards;
    }

    /// En el caso en el que una instancia no este inicializada

    public int MAX_BOARDS_PER_USER() { return MAX_BOARDS_PER_USER; }
    public int MAX_USERS_PER_LOBBY() { return MAX_USERS_PER_LOBBY; }
    public int MAX_PIECES_PER_BOARDS() { return MAX_PIECES_PER_BOARDS; }

    /// Lobbies logic -----------------------------------------------------------------------------------------------------------

    public List<Lobby> getPublicLobbies() {
        return _lobbiesRep.findAllOpen();
    }

    public Lobby createLobby(@Email @NotBlank String userId, boolean open, @NotNull String password)
            throws LobbyRegistrationException {
        if (_lobbiesRep.existByUserIdsContaining(userId))
            throw new LobbyRegistrationException();

        Lobby lobby = new Lobby(userId, open, password);

        lobby = _lobbiesRep.insert(lobby);

        return lobby;
    }

    public Lobby getLobby(@NotBlank String id)
            throws LobbyRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            return lobby;
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby joinLobby(@Email @NotBlank String userId, @NotBlank String id, @NotNull String password)
            throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            lobby.addUser(userId, password);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Lobby> leaveLobby(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            lobby.removeUser(userId);

            if (lobby.isEmpty()) {
                _lobbiesRep.deleteById(lobby.getId());
                return Optional.empty();
            }

            lobby.getBoard().deselectUserPiece(userId);

            return Optional.of(_lobbiesRep.save(lobby));
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /// Boards game logic -----------------------------------------------------------------------------------------------------------

    public Lobby addPiece(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new RuntimeException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            lobby.getBoard().addPiece();

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby updatePieceProperties_hp_maxHp(@Email @NotBlank String userId, @NotBlank String id,
                                                @Valid @NotNull Piece piece,
                                                @Positive int hp,
                                                @PositiveOrZero int maxHp)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);
            piece.setHp(hp);
            piece.setMaxHp(maxHp);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby updatePieceProperties_image(@Email @NotBlank String userId, @NotBlank String id,
                                             @Valid @NotNull Piece piece,
                                             @NotNull String imageId)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);
            piece.setImageId(imageId);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby updatePieceProperties_pos(@Email @NotBlank String userId, @NotBlank String id,
                                           @Valid @NotNull Piece piece,
                                           @DecimalMin(value = "0.0", inclusive = true) @DecimalMax(value = "1.0", inclusive = true) float xPos,
                                           @DecimalMin(value = "0.0", inclusive = true) @DecimalMax(value = "1.0", inclusive = true) float yPos)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);

            if (!piece.getCurrentUser().isEmpty())
                throw new InvalidOperationException();

            piece.setX(xPos);
            piece.setY(yPos);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


    public Lobby selectPiece(@Email @NotBlank String userId, @NotBlank String id,
                             @Valid @NotNull Piece piece)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);

            if (!piece.getCurrentUser().isEmpty())
                throw new InvalidOperationException();

            piece.setCurrentUser(userId);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby deselectPiece(@Email @NotBlank String userId, @NotBlank String id,
                               @Valid @NotNull Piece piece)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);

            if (!piece.getCurrentUser().equals(userId))
                throw new InvalidOperationException();

            piece.setCurrentUser("");
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby removePiece(@Email @NotBlank String userId, @NotBlank String id,
                             @Valid @NotNull Piece piece)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            piece = lobby.getBoard().findPiece(piece.getId()).orElseThrow(PieceRegistrationException::new);

            if (!piece.getCurrentUser().equals(userId))
                throw new InvalidOperationException();

            lobby.getBoard().removePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby changeBoard(@Email @NotBlank String userId, @NotBlank String id,
                             @Valid @NotNull Board board)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
            Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
            try {
                if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                    throw new IllegalStateException(); // Lock could not be acquired
                // Critical section - start
                Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

                if (!lobby.contains(userId))
                    throw new UserRegistrationException();

                lobby.setBoard(board);

                return _lobbiesRep.save(lobby);
                // Critical section - end
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
    }

    public Lobby modifyBoardProperties(@Email @NotBlank String userId, @NotBlank String id,
                                       @NotNull String imageId, int scale)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            lobby.getBoard().setBackgroundImage(imageId);
            lobby.getBoard().setScale(scale);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Lobby clearBoard(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            lobby.getBoard().clear();

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /// Boards persistence logic -----------------------------------------------------------------------------------------------------------

    public List<Board> getSavedBoards(@Email @NotBlank String userId) {
        return _boardsRep.findByUserId(userId);
    }

    public Board addBoard(@Email @NotBlank String userId, @Valid @NotNull Board board) throws BoardRegistrationException {
        if (_boardsRep.findByUserId(userId).size() >= MAX_BOARDS_PER_USER)
            throw new BoardRegistrationException();

        board.initId();
        board.setUserId(userId);

        return _boardsRep.insert(board);
    }

    public Board saveBoard(@Email @NotBlank String userId, @NotBlank String id, @Valid @NotNull Board board) throws BoardRegistrationException, InvalidOperationException {
        Board savedBoard = _boardsRep.findById(id).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        board.setId(id);
        board.setUserId(userId);

        return _boardsRep.save(board);
    }

    public void removeBoard(@Email @NotBlank String userId, @NotBlank String id) throws BoardRegistrationException, InvalidOperationException {
        Board savedBoard = _boardsRep.findById(id).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        _boardsRep.delete(savedBoard);
    }
}
