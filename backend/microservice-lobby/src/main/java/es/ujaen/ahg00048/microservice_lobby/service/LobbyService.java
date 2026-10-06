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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
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

    private final static String LOCK_LOBBY_KEY_BASE = "lobbies:";
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
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            return lobby;
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Lobby joinLobby(@Email @NotBlank String userId, @NotBlank String id, @NotNull String password)
            throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            lobby.addUser(userId, password);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Optional<Lobby> leaveLobby(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
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
            if (locked)
                lock.unlock();
        }
    }

    /// Boards game logic -----------------------------------------------------------------------------------------------------------

    public Lobby addPiece(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new RuntimeException(); // Lock could not be acquired
            locked = true;
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
            if (locked)
                lock.unlock();
        }
    }

    public Lobby updatePieceProperties_hp_maxHp(@Email @NotBlank String userId, @NotBlank String id,
                                                int pieceId,
                                                @Positive int hp,
                                                @PositiveOrZero int maxHp)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);
            piece.setHp(hp);
            piece.setMaxHp(maxHp);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Lobby updatePieceProperties_image(@Email @NotBlank String userId, @NotBlank String id,
                                             int pieceId,
                                             @NotNull String imageId)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);
            piece.setImageId(imageId);
            lobby.getBoard().updatePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Lobby updatePieceProperties_pos(@Email @NotBlank String userId, @NotBlank String id,
                                           int pieceId,
                                           @DecimalMin(value = "0.0", inclusive = true) @DecimalMax(value = "1.0", inclusive = true) float xPos,
                                           @DecimalMin(value = "0.0", inclusive = true) @DecimalMax(value = "1.0", inclusive = true) float yPos)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);

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
            if (locked)
                lock.unlock();
        }
    }


    public Lobby selectPiece(@Email @NotBlank String userId, @NotBlank String id,
                             int pieceId)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);

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
            if (locked)
                lock.unlock();
        }
    }

    public Lobby deselectPiece(@Email @NotBlank String userId, @NotBlank String id,
                               int pieceId)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);

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
            if (locked)
                lock.unlock();
        }
    }

    public Lobby removePiece(@Email @NotBlank String userId, @NotBlank String id,
                             int pieceId)
            throws LobbyRegistrationException, UserRegistrationException, PieceRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Piece piece = lobby.getBoard().findPiece(pieceId).orElseThrow(PieceRegistrationException::new);

            if (!piece.getCurrentUser().equals(userId))
                throw new InvalidOperationException();

            lobby.getBoard().removePiece(piece);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Lobby changeBoard(@Email @NotBlank String userId, @NotBlank String id,
                             @NotBlank String boardId)
            throws LobbyRegistrationException, UserRegistrationException, BoardRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
            // Critical section - start
            Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

            if (!lobby.contains(userId))
                throw new UserRegistrationException();

            Board board = _boardsRep.findById(boardId).orElseThrow(BoardRegistrationException::new);

            board.setId("");
            lobby.setBoard(board);

            return _lobbiesRep.save(lobby);
            // Critical section - end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public Lobby modifyBoardProperties(@Email @NotBlank String userId, @NotBlank String id,
                                       @NotNull String imageId, int scale)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
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
            if (locked)
                lock.unlock();
        }
    }

    public Lobby clearBoard(@Email @NotBlank String userId, @NotBlank String id)
            throws LobbyRegistrationException, UserRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException(); // Lock could not be acquired
            locked = true;
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
            if (locked)
                lock.unlock();
        }
    }

    /// Boards persistence logic -----------------------------------------------------------------------------------------------------------

    public List<Board> getSavedBoards(@Email @NotBlank String userId) {
        return _boardsRep.findByUserId(userId);
    }

    public Board addBoard(@Email @NotBlank String userId, @Valid @NotNull Board board)
            throws BoardRegistrationException,
            IllegalStateException {
        if (_boardsRep.findByUserId(userId).size() >= MAX_BOARDS_PER_USER)
            throw new BoardRegistrationException();

        board.initId();
        board.setUserId(userId);

        return _boardsRep.insert(board);
    }

    public Board saveBoard(@Email @NotBlank String userId, @NotBlank String id, @Valid @NotNull Board board)
            throws BoardRegistrationException, InvalidOperationException,
            IllegalStateException {
        Board savedBoard = _boardsRep.findById(id).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        board.setId(id);
        board.setUserId(userId);
        board.setVersion(savedBoard.getVersion());

        return _boardsRep.save(board);
    }

    public void removeBoard(@Email @NotBlank String userId, @NotBlank String id)
            throws BoardRegistrationException, InvalidOperationException,
            IllegalStateException {
        Board savedBoard = _boardsRep.findById(id).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        _boardsRep.delete(savedBoard);
    }

    /// Used by RabbitMQ listener --------------------------------------------------------------------------------------------------------------------

    @Transactional(noRollbackFor = UserRegistrationException.class)
    public void removeUser_admin(String userId)
            throws UserRegistrationException,
            IllegalStateException {
        _boardsRep.deleteAllByUserId(userId);

        Lobby lobby = _lobbiesRep.findByUserId(userId).orElseThrow(UserRegistrationException::new);

        Lock lock1 = _lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + lobby.getId());
        boolean locked = false;
        try {
            if (!lock1.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            lobby = _lobbiesRep.findByUserId(userId).orElseThrow(UserRegistrationException::new);

            lobby.removeUser(userId);

            _lobbiesRep.save(lobby);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock1.unlock();
        }
    }

    @Transactional
    public void removeImage_admin(String imageId)
            throws IllegalStateException {
        List<Board> boards = _boardsRep.findAllContainingImageId(imageId);

        for (Board board : boards) {
            removeImageFromBoard_admin(board, imageId);
        }

        _boardsRep.saveAll(boards);

        List<Lobby> lobbies = _lobbiesRep.findAllByImageId(imageId);
        List<Lock> locks = new ArrayList<>(lobbies.size());

        for (Lobby lobby : lobbies) {
            locks.add(_lockRegistry.obtain(LOCK_LOBBY_KEY_BASE + lobby.getId()));
        }

        int locked = 0;
        try {
            while (locked < locks.size()) {
                if (!locks.get(locked).tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                    throw new IllegalStateException();

                locked++;
            }

            lobbies = _lobbiesRep.findAllByImageId(imageId);
            for (Lobby lobby : lobbies) {
                removeImageFromBoard_admin(lobby.getBoard(), imageId);
            }

            _lobbiesRep.saveAll(lobbies);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            for (int i = 0; i < locked; i++) {
                locks.get(i).unlock();
            }
        }
    }

    private void removeImageFromBoard_admin(Board board, String imageId) {
        if (board.getBackgroundImage().equals(imageId)) {
            board.setBackgroundImage("");
        }

        board.setPieces(board.getPieces()
                .stream()
                .peek(p -> {
                    if (p.getImageId().equals(imageId))
                        p.setImageId("");
                })
                .toList());
    }
}