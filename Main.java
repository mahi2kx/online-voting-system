package voting;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main.java
 * ----------
 * Entry point for the Online Voting System.
 *
 * Presents a console-based menu driven by a Scanner.
 * Delegates all database work to the respective DAO classes.
 *
 * Menu options:
 *   1. Register Voter
 *   2. Login Voter
 *   3. Add Candidate
 *   4. View Candidates
 *   5. Cast Vote      (requires login)
 *   6. View Results
 *   7. Exit
 */
public class Main {

    // ── DAO instances (created once, shared throughout the session) ────────
    private static final VoterDAO     voterDAO     = new VoterDAO();
    private static final CandidateDAO candidateDAO = new CandidateDAO();
    private static final VoteDAO      voteDAO      = new VoteDAO();

    // Currently logged-in voter (null means not logged in)
    private static Voter loggedInVoter = null;

    // Console input scanner
    private static final Scanner scanner = new Scanner(System.in);

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1  -> registerVoter();
                case 2  -> loginVoter();
                case 3  -> addCandidate();
                case 4  -> viewCandidates();
                case 5  -> castVote();
                case 6  -> viewResults();
                case 7  -> { running = false; exitApp(); }
                default -> System.out.println("[!] Invalid option. Please choose 1-7.");
            }
        }
    }

    // =========================================================================
    // MENU DISPLAY
    // =========================================================================

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       ONLINE VOTING SYSTEM  v1.0         ║");
        System.out.println("║      Built with Java + JDBC + MySQL      ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("══════════════ MAIN MENU ══════════════");
        if (loggedInVoter != null) {
            System.out.println("  Logged in as: " + loggedInVoter.getName());
        }
        System.out.println("  1. Register Voter");
        System.out.println("  2. Login");
        System.out.println("  3. Add Candidate");
        System.out.println("  4. View Candidates");
        System.out.println("  5. Cast Vote        [login required]");
        System.out.println("  6. View Results");
        System.out.println("  7. Exit");
        System.out.println("═══════════════════════════════════════");
    }

    // =========================================================================
    // FEATURE 1 – REGISTER VOTER
    // =========================================================================

    private static void registerVoter() {
        System.out.println("\n--- REGISTER NEW VOTER ---");

        System.out.print("  Full Name    : ");
        String name = scanner.nextLine().trim();

        System.out.print("  Username     : ");
        String username = scanner.nextLine().trim();

        System.out.print("  Password     : ");
        String password = scanner.nextLine().trim();

        // Basic validation
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("[!] All fields are required.");
            return;
        }

        Voter voter = new Voter(name, username, password);

        if (voterDAO.registerVoter(voter)) {
            System.out.println("[✓] Voter registered successfully! You can now login.");
        } else {
            System.out.println("[✗] Registration failed. Try a different username.");
        }
    }

    // =========================================================================
    // FEATURE 2 – LOGIN VOTER
    // =========================================================================

    private static void loginVoter() {
        if (loggedInVoter != null) {
            System.out.println("[i] You are already logged in as: " + loggedInVoter.getName());
            return;
        }

        System.out.println("\n--- VOTER LOGIN ---");

        System.out.print("  Username : ");
        String username = scanner.nextLine().trim();

        System.out.print("  Password : ");
        String password = scanner.nextLine().trim();

        Voter voter = voterDAO.loginVoter(username, password);

        if (voter != null) {
            loggedInVoter = voter;
            System.out.println("[✓] Login successful! Welcome, " + voter.getName() + ".");

            // Inform if the voter has already voted this session
            if (voteDAO.hasVoted(voter.getId())) {
                System.out.println("[i] Note: You have already cast your vote in this election.");
            }
        } else {
            System.out.println("[✗] Invalid username or password. Please try again.");
        }
    }

    // =========================================================================
    // FEATURE 3 – ADD CANDIDATE
    // =========================================================================

    private static void addCandidate() {
        System.out.println("\n--- ADD CANDIDATE ---");

        System.out.print("  Candidate Name  : ");
        String name = scanner.nextLine().trim();

        System.out.print("  Party           : ");
        String party = scanner.nextLine().trim();

        if (name.isEmpty() || party.isEmpty()) {
            System.out.println("[!] Candidate name and party are required.");
            return;
        }

        Candidate candidate = new Candidate(name, party);

        if (candidateDAO.addCandidate(candidate)) {
            System.out.println("[✓] Candidate '" + name + "' added successfully.");
        } else {
            System.out.println("[✗] Failed to add candidate.");
        }
    }

    // =========================================================================
    // FEATURE 4 – VIEW CANDIDATES
    // =========================================================================

    private static void viewCandidates() {
        System.out.println("\n--- CANDIDATES LIST ---");

        List<Candidate> candidates = candidateDAO.getAllCandidates();

        if (candidates.isEmpty()) {
            System.out.println("[i] No candidates found. Add candidates first.");
            return;
        }

        System.out.println();
        System.out.printf("  %-5s %-25s %s%n", "ID", "Name", "Party");
        System.out.println("  " + "-".repeat(55));

        for (Candidate c : candidates) {
            System.out.printf("  %-5d %-25s %s%n", c.getId(), c.getName(), c.getParty());
        }
        System.out.println("  " + "-".repeat(55));
        System.out.println("  Total candidates: " + candidates.size());
    }

    // =========================================================================
    // FEATURE 5 – CAST VOTE
    // =========================================================================

    private static void castVote() {
        // Must be logged in
        if (loggedInVoter == null) {
            System.out.println("[!] You must LOGIN before casting a vote. Choose option 2.");
            return;
        }

        // Duplicate vote guard
        if (voteDAO.hasVoted(loggedInVoter.getId())) {
            System.out.println("[!] You have already voted. Each voter may vote only once.");
            return;
        }

        // Show current candidates
        viewCandidates();

        List<Candidate> candidates = candidateDAO.getAllCandidates();
        if (candidates.isEmpty()) {
            System.out.println("[i] No candidates available to vote for.");
            return;
        }

        System.out.println("\n--- CAST YOUR VOTE ---");
        int candidateId = readInt("  Enter the Candidate ID you wish to vote for: ");

        // Validate candidate id
        Candidate chosen = candidateDAO.getCandidateById(candidateId);
        if (chosen == null) {
            System.out.println("[!] Invalid Candidate ID. Please choose from the list above.");
            return;
        }

        // Confirm before committing
        System.out.printf("  You are about to vote for: %s (%s)%n",
                          chosen.getName(), chosen.getParty());
        System.out.print("  Confirm vote? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("[i] Vote cancelled.");
            return;
        }

        // Record the vote
        Vote vote = new Vote(loggedInVoter.getId(), candidateId);

        if (voteDAO.castVote(vote)) {
            System.out.println("[✓] Your vote for " + chosen.getName() + " has been recorded. Thank you!");
        } else {
            System.out.println("[✗] Vote could not be recorded. Please try again.");
        }
    }

    // =========================================================================
    // FEATURE 6 – VIEW RESULTS
    // =========================================================================

    private static void viewResults() {
        System.out.println("\n--- ELECTION RESULTS ---");

        int totalVotes = voteDAO.getTotalVotes();

        if (totalVotes == 0) {
            System.out.println("[i] No votes have been cast yet.");
            return;
        }

        Map<String, Integer> results = voteDAO.getResults();

        System.out.println();
        System.out.printf("  %-40s %10s  %s%n", "Candidate (Party)", "Votes", "Share");
        System.out.println("  " + "-".repeat(65));

        String winner     = null;
        int    maxVotes   = -1;

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            String  candidate = entry.getKey();
            int     votes     = entry.getValue();
            double  share     = (totalVotes > 0) ? (votes * 100.0 / totalVotes) : 0;
            String  bar       = "█".repeat((int)(share / 5)); // simple bar graph

            System.out.printf("  %-40s %10d  %.1f%% %s%n", candidate, votes, share, bar);

            if (votes > maxVotes) {
                maxVotes = votes;
                winner   = candidate;
            }
        }

        System.out.println("  " + "-".repeat(65));
        System.out.printf("  Total votes cast: %d%n", totalVotes);
        System.out.println();

        if (winner != null && maxVotes > 0) {
            System.out.println("  🏆 LEADING CANDIDATE: " + winner + " with " + maxVotes + " vote(s)");
        }
    }

    // =========================================================================
    // EXIT
    // =========================================================================

    private static void exitApp() {
        System.out.println("\n[i] Closing database connection...");
        DBConnection.closeConnection();
        System.out.println("[✓] Thank you for using the Online Voting System. Goodbye!");
        scanner.close();
    }

    // =========================================================================
    // UTILITY – safe integer input reader
    // =========================================================================

    /**
     * Prompts the user and reads an integer, re-prompting on invalid input.
     *
     * @param  prompt  the message to display before reading
     * @return the integer entered by the user
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid number.");
            }
        }
    }
}
