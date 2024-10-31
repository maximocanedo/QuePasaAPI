package quepasa.api.verifiers;

public interface UserVerifier {
    boolean existsByUsername(String username);
}
