package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;

import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Document("boards")
public class Board implements Serializable {
    // persist
    @Indexed(unique = false)
    private String userId = "";
    @Id
    private String id = "";

    @NotNull
    private String backgroundImage = "";
    private final List<Piece> pieces;
    @Min(value = 10)
    @Max(value = 500)
    private int scale = 10;


    public Board() {
        pieces = new ArrayList<>();
    }

    public Board(Board other ) {
        userId = other.userId;
        id = other.id;
        backgroundImage = other.backgroundImage;
        pieces = new ArrayList<>(other.pieces);
        scale = other.scale;
    }


    public void initId() {
        id = new ObjectId().toString();
    }


    public void addPiece() throws InvalidOperationException {
        if (pieces.size() >= LobbyService.MAX_PIECES_PER_BOARDS)
            throw new InvalidOperationException();

        int id = 0;
        Piece newPiece = new Piece(id);
        while (pieces.contains(newPiece)) {
            id++;
            newPiece.setId(id);
        }

        pieces.add(newPiece);
    }

    public void removePiece(Piece piece) throws InvalidOperationException {
        if (!pieces.contains(piece))
            throw new InvalidOperationException();

        pieces.remove(piece);
    }

    public void updatePiece(Piece piece) throws InvalidOperationException {
        if (!pieces.contains(piece))
            throw new InvalidOperationException();

        pieces.set(pieces.indexOf(piece), piece);
    }

    public void clear() { pieces.clear(); }
}
