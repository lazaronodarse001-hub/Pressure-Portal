package arpm.store;

import arpm.model.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file-backed store for Client records.
 * Data is saved to a file in the user's home directory so it persists between runs.
 */
public class ClientStore {

    private final Path filePath;
    private List<Client> clients = new ArrayList<>();

    public ClientStore() {
        Path dir = Paths.get(System.getProperty("user.home"), ".arpm");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            // fall back to working directory if home dir isn't writable
        }
        this.filePath = dir.resolve("clients.dat");
        load();
    }

    public List<Client> getAll() {
        return clients;
    }

    public void add(Client client) {
        clients.add(client);
        save();
    }

    public void remove(Client client) {
        clients.remove(client);
        save();
    }

    /** Call after mutating fields on an existing Client object in place. */
    public void update() {
        save();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                clients = (List<Client>) obj;
            }
        } catch (Exception e) {
            System.err.println("Could not load client data: " + e.getMessage());
            clients = new ArrayList<>();
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(clients);
        } catch (IOException e) {
            System.err.println("Could not save client data: " + e.getMessage());
        }
    }
}
