""" holds the Piece class """
from mygame import pygame
from main import PIECE_COLORS, PIECE_RADIUS, PIECE_OUTLINE

class Piece:
    """ a piece for the game """
    def __init__(self, surface, player, center):
        self.surface = surface
        self.player = player
        self.center = center

    def __str__(self):
        return str(self.player) + " - " + str(self.center)

    def draw(self):
        """ draws the piece on the board """
        pygame.draw.circle(self.surface, PIECE_COLORS[1-self.player], self.center, PIECE_OUTLINE)
        pygame.draw.circle(self.surface, PIECE_COLORS[self.player], self.center, PIECE_RADIUS)

    def get_diff(self, point):
        """ returns differect vector of point from center """
        return point[0] - self.center[0], point[1] - self.center[1]

    def move(self, vect):
        """ moves the center towards vect """
        self.center[0] += vect[0]
        self.center[1] += vect[1]
