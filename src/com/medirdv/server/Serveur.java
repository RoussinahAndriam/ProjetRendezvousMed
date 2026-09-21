package com.medirdv.server;

import com.medirdv.dao.MedecinDAO;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Point d'entrée du Serveur MédiRDV.
 * Lance un thread par client connecté.
 * Port : 5000
 */
public class Serveur {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        MedecinDAO dao = new MedecinDAO();

        System.out.println("╔════════════════════════════════╗");
        System.out.println("║   Serveur MédiRDV démarré      ║");
        System.out.println("║   Port : " + PORT + "                   ║");
        System.out.println("╚════════════════════════════════╝");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Un thread par client → plusieurs clients simultanés
                Thread t = new Thread(new ClientHandler(clientSocket, dao));
                t.setDaemon(true);
                t.start();
            }
        } catch (Exception e) {
            System.err.println("[SERVEUR] Erreur fatale : " + e.getMessage());
        }
    }
}
