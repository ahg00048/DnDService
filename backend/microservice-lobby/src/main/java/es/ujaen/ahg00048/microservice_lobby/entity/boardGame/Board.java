package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;

import jakarta.validation.constraints.*;
import lombok.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.ujaen.ahg00048.microservice_lobby.exception.PieceRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;


@Getter
@Setter
@AllArgsConstructor
@Document("boards")
public class Board implements Serializable {
    // persist
    @Indexed(unique = false)
    private String userId = "";
    @Id
    private String id = "";

    @Version
    private Long version;

    @NotNull
    private String backgroundImage = "";
    @Min(value = 10)
    @Max(value = 500)
    private int scale = 10;

    private List<Piece> pieces;


    public Board() {
        pieces = new ArrayList<>();
    }

    public Board(Board other) {
        userId = other.userId;
        version = other.version;
        id = other.id;
        backgroundImage = other.backgroundImage;
        pieces = new ArrayList<>(other.pieces);
        scale = other.scale;
    }

    public Board(String userId, String id, String backgroundImage, int scale, List<Piece> pieces) {
        this.userId = userId;
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.scale = scale;
        this.pieces = pieces;
    }


    public void initId() {
        id = new ObjectId().toString();
    }


    public void addPiece() throws PieceRegistrationException {
        if (pieces.size() >= LobbyService.MAX_PIECES_PER_BOARDS)
            throw new PieceRegistrationException();

        int id = 0;
        Piece newPiece = new Piece(id);
        while (pieces.contains(newPiece)) {
            id++;
            newPiece.setId(id);
        }

        pieces.add(newPiece);
    }

    public void removePiece(Piece piece) throws PieceRegistrationException {
        if (!pieces.contains(piece))
            throw new PieceRegistrationException();

        pieces.remove(piece);
    }

    public void updatePiece(Piece piece) throws PieceRegistrationException {
        if (!pieces.contains(piece))
            throw new PieceRegistrationException();

        pieces.set(pieces.indexOf(piece), piece);
    }

    public Optional<Piece> findPiece(int id) throws PieceRegistrationException {
        for (Piece p : pieces) {
            if (p.getId() == id)
                return Optional.of(p);
        }

        return Optional.empty();
    }

    public void deselectUserPiece(String userId) {
        for (Piece p : pieces) {
            if (p.getCurrentUser().equals(userId))
                p.setCurrentUser("");
        }
    }

    public void clear() { pieces.clear(); }
}
