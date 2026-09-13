package es.ujaen.ahg00048.microservice_lobby.rest.DTO.boardGame;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import jakarta.validation.constraints.*;


public record PieceDTO(int id, String currentUser, float x, float y, String imageId, int hp, int maxHp) {
    public PieceDTO(Piece piece) {
        this(piece.getId(), piece.getCurrentUser(), piece.getX(), piece.getY(), piece.getImageId(), piece.getHp(), piece.getMaxHp());
    }
}
