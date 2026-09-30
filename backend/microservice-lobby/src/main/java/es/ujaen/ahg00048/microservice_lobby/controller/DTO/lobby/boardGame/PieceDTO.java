package es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;


public record PieceDTO(int id, String currentUser, float x, float y, String imageId, int hp, int maxHp) {
    public PieceDTO(Piece piece) {
        this(piece.getId(), piece.getCurrentUser(), piece.getX(), piece.getY(), piece.getImageId(), piece.getHp(), piece.getMaxHp());
    }
}
