package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;

import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Board {
    // persist
    private String userId;
    private String id;

    private String background;
    private final List<Piece> pieces;
    private int scale;


    public Board(int scale, String background) {
        pieces = new ArrayList<>();
        userId = "";
        id = "";

        this.background = background;
        this.scale = scale;
    }

    public Board(int scale) {
        pieces = new ArrayList<>();
        background = "";
        userId = "";
        id = "";

        this.scale = scale;
    }


    public void initId() {
        id = new ObjectId().toString();
    }


    public void addPiece() throws BoardRegistrationException {
        if (pieces.size() >= LobbyService.MAX_PIECES_PER_BOARDS)
            throw new BoardRegistrationException();

        int id = 0;
        Piece newPiece = new Piece(id);
        while (pieces.contains(newPiece)) {
            id++;
            newPiece.setId(id);
        }

        pieces.add(newPiece);
    }

    public void removePiece(Piece piece) throws BoardRegistrationException {
        if (!pieces.contains(piece))
            throw new BoardRegistrationException();

        pieces.remove(piece);
    }

    public void updatePiece(Piece piece) throws BoardRegistrationException {
        if (!pieces.contains(piece))
            throw new BoardRegistrationException();

        pieces.set(pieces.indexOf(piece), piece);
    }

    public void clear() { pieces.clear(); }
}
