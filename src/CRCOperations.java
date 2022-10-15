import java.util.Random;

/**
 * Η κλάση στην οποία υπάρχουν οι μέθοδοι οι οποίες υλοποιούν τα ζητούμενα της εργασίας.
 */
public class CRCOperations {

    /**
     * Δημιουργεί τυχαία δυαδικά μηνύματα των k bits (μπλοκ δεδομένων των k bits, σε κάθε bit των
     * οποίων, το 0 και το 1 έχουν ίση πιθανότητα εμφάνισης).
     * @param k αριθμός bit δυαδικού μηνύματος.
     * @return τυχαία επιλεγμένο δυαδικό μήνυμα k των bits.
     */
    public static long generateRandomMessage(int k){
        // Η γεννήτρια τυχαίων αριθμών για επιλογή του κάθε bit.
        Random generator = new Random();

        // Το τυχαίο μήνυμα ξεκινάει με 0 και τα ψηφία του θα "προστεθούν" ένα-ένα, σταδιακά.
        long result = 0;

        for (; k > 0; k--){
            // Shift αριθμού αριστερά κατά μία θέση για αργότερη προσθήκη νέου bit στο "κενό" που 
            // δημιουργείται στο τελευταίο ψηφίο του αριθμού με την πράξη της ολίσθησης.
            result <<= 1;
            
            // Εμφάνιση bit 1 λόγω πιθανότητας:
            if (generator.nextDouble() > 0.5){
                // "Προσθήκη" 1 στο τέλος του αριθμού.
                // Η πράξη δυαδικού or του αριθμού με το 1 εξασφαλίζει "τοποθέτηση" ψηφίου 1 στην
                // "κενή" θέση στο τέλος του αριθμού.
                result |= 1;
            }

            // Εμφάνιση bit 0 λόγω πιθανότητας:
            // Αν δεν ισχύει η συνθήκη του if, δεν αλλάζω κάτι στον αριθμό, νοητή "προσθήκη" 0 στο τέλος του
            // αριθμού (στην "κενή" θέση) ήδη από την πράξη shift.
        }

        return result;
    }

    /**
     * Υλοποιεί διαίρεση modulo-2 αριθμητικής των δύο αριθμών και επιστρέφει το υπόλοιπο.
     * @param dividend διαιρετέος.
     * @param divisor διαιρέτης.
     * @return υπόλοιπο modulo-2 διαίρεσης των δύο αριθμών.
     */
    private static long XORDivisionRemainder(long dividend, long divisor){
        // Μετατροπή του διαιρετέου σε ατομικά bits ώστε αργότερα να μπορώ να τα "κατεβάζω" ένα-ένα.
        String dividendBits = Long.toBinaryString(dividend);

        // Αποθήκευση του πλήθους bit (μήκους) του διαιρέτη.
        int divisorBitsLength = Long.toBinaryString(divisor).length();

        // Αποθηκεύω την θέση του πιο αριστερού bit του διαιρέτη ώστε αργότερα να μπορώ να
        // εξασφαλίσω ότι οι αριθμοί στους οποίους κάνω XOR είναι ευθυγραμμισμένοι.
        long divisorLeftmostBit = 1L << divisorBitsLength - 1;

        // Διαχείριση περίπτωσης όπου πλήθος bits διαιρέτη > πλήθος bits διαιρετέου.
        // Σε αυτή την περίπτωση η διαίρεση έχει τελειώσει και το υπόλοιπο είναι ο ίδιος ο 
        // διαιρετέος.
        if (dividendBits.length() < divisorBitsLength){
            return dividend;
        }

        // Δείκτης θέσης του bit του διαιρετέου, που "δείχνει" ποιο είναι επόμενο για να "κατέβει".
        // Σημειώνεται ότι εφόσον τα bit του διαιρετέου είναι αποθηκευμένα σε συμβολοσειρά ως χαρακτήρες, 
        // η αρίθμηση των bit γίνεται από το MSB προς το LSB (δηλαδή από αριστερά προς τα δεξιά) αρχίζοντας με
        // θέση του MSB το 0.
        //
        // Αρχική τιμή το πλήθος των bit του διαιρέτη που "δείχνει" ως επόμενο bit για να "κατέβει"
        // εκείνο της θέσης με τιμή την επόμενη του μήκους του διαιρέτη. Αυτό εφόσον, ο διαιρέτης, 
        // στην αρχή, θα έχει ευθυγραμμιστεί με τα πρώτα m bits (έστω διαιρέτης μήκους m bits) του διαιρετέου 
        // και το επόμενο θα είναι το (m+1)-ο bit. Εφόσον όμως η αρίθμηση θέσεων ξεκινάει από το 0 ο αριθμός των 
        // bit του διαιρέτη μας δίνει την θέση bit που χρειαζόμαστε.
        int dividendBitsIterator = divisorBitsLength;

        // Shift του διαιρετέου προς τα δεξιά ώστε τα πρώτα bits διαιρέτη και διαιρετέου να ευθυγραμμιστούν.
        //
        // Ο αριθμός που μένει είναι τα πρώτα m bits του διαιρετέου εφόσον (έστω m αριθμός bit διαιρέτη και p αριθμό
        // bit διαιρετέου):
        // dividendBits.length() - divisorBitsLength = p - m και θα μείνουν τα πρώτα p - (p - m) = m bits.
        long remainder = dividend >> dividendBits.length() - divisorBitsLength;

        // Όσο υπάρχουν ψηφία (bits) που δεν έχουν ακόμα "κατέβει" συνεχίζω τις πράξεις XOR.
        while (dividendBitsIterator < dividendBits.length()){
            // Πράξη XOR μεταξύ επιμέρους ποσοτήτων. Ανάλογη (της modulo 2 διαίρεσης) πράξη με την αφαίρεση
            // στην συνηθισμένη διαίρεση.
            remainder ^= divisor;

            // Όσο διαιρέτης και τρέχον υπόλοιπο δεν είναι ευθυγραμμισμένα και απομένουν ψηφία του διαιρετέου που
            // μπορούν να "κατέβουν", "κατεβάζω" ψηφία από τον διαιρετέο στο τρέχον υπόλοιπο.
            //
            // Διαιρέτης και τρέχον υπόλοιπο είναι ευθυγραμμισμένα όταν το bit του διαιρετέου στην αντίστοιχη θέση
            // εκείνης του πιο αριστερού bit του διαιρέτη είναι 1.
            while ((remainder & divisorLeftmostBit) == 0 && dividendBitsIterator < dividendBits.length()){
                // "Κάνω χώρο" για το εισερχόμενο bit που θα μπει στο τέλος του τρέχοντος υπολοίπου.
                remainder <<= 1;
                // Κατεβάζω το επόμενο ψηφίο από τον διαιρετέο και τον τοποθετώ στο τέλος του τρέχοντος υπολοίπου.
                remainder |= dividendBits.charAt(dividendBitsIterator++)=='0'?0:1;
            }
        }

        // Ελέγχω την περίπτωση να έχουν τελειώσει τα ψηφία που μπορούν να κατέβουν αλλά μπορεί να γίνει
        // ακόμα μία πράξη XOR.
        if ((remainder & divisorLeftmostBit) != 0){
            remainder ^= divisor;
        }

        // Έχουν τελειώσει διαθέσιμα ψηφία που μπορούν να κατέβουν και το τρέχον υπόλοιπο είναι το τελικό.
        return remainder;
    }

    /**
     * Υπολογίζει και επιστρέφει την ακολουθία κώδικα CRC (FCS) του δοθέντος μηνύματος.
     * @param D η προς μετάδοση ακολουθία δεδομένων (μήνυμα) των k bits αποθηκευμένη σε (μεγάλο) ακέραιο.
     * @param P ο προκαθορισμένος αριθμός των n-k+1 bits με τον οποίο θα πρέπει να είναι διαιρέσιμη η ακολουθία Τ.
     * (πρότυπο για τον υπολογισμό του CRC).
     * @return τον κώδικα CRC (FCS) του δοθέντος μηνύματος.
     */
    public static long calculateCRC(long D, long P){
        // Αποθηκεύω την τιμή 2^(n-k)*D που θα χρειαστεί στην παραγωγή του CRC.
        // Για να το κάνω αυτό, τοποθετώ n-k μηδενικά στα δεξιά του D για να προκύψει το 2^(n-k)*D.
        // Η ακολουθία P έχει n - k + 1 bits, επομένως τοποθετώ n - k + 1 - 1 = n - k μηδενικά στο τέλος του D.
        long DShifted = D << Long.toBinaryString(P).length() - 1;

        // Δημιουργώ την ακολουθία CRC (FCS).
        // Η μεταβλητή DShifted είναι ο D στον οποίο έχουν τοποθετηθεί n-k (πλήθος bits P - 1 = n-k+1-1 = n - k) μηδενικά.
        // Η ακολουθία CRC (FCS) είναι το υπόλοιπο της διαίρεσης (modulo-2) του DShifted με το P.
        return XORDivisionRemainder(DShifted, P);
    }

    /**
     * Υπολογίζει και επιστρέφει την τελική ακολουθία προς μετάδοση η οποία αποτελείται του δοθέντος μηνύματος ακολουθούμενη 
     * απο τα bits του CRC (FCS).
     * @param D η προς μετάδοση ακολουθία δεδομένων (μήνυμα) των k bits αποθηκευμένη σε (μεγάλο) ακέραιο.
     * @param P ο προκαθορισμένος αριθμός των n-k+1 bits με τον οποίο θα πρέπει να είναι διαιρέσιμη η ακολουθία Τ.
     * (πρότυπο για τον υπολογισμό του CRC).
     * @return την τελική ακολουθία προς μετάδοση.
     */
    public static long calculateT(long D, long P){
        // Αποθηκεύω την τιμή 2^(n-k)*D που θα χρειαστεί στην παραγωγή του T.
        // Για να το κάνω αυτό, τοποθετώ n-k μηδενικά στα δεξιά του D για να προκύψει το 2^(n-k)*D.
        // Η ακολουθία P έχει n - k + 1 bits, επομένως τοποθετώ n - k + 1 - 1 = n - k μηδενικά στο τέλος του D.
        long DShifted = D << Long.toBinaryString(P).length() - 1;

        // Δημιουργώ την ακολουθία CRC (FCS).
        // Η ακολουθία CRC (FCS) είναι το υπόλοιπο της διαίρεσης (modulo-2) του DShifted με το P.
        long FCS = calculateCRC(D, P);

        // Η ακολουθία T παράγεται με την πράξη 2^(n-k)*D + FCS.
        return DShifted + FCS;
    }

    /**
     * Ελέγχει την εγκυρότητα της ακολουθίας CRC με βάση τον αριθμό P.
     * Έχει ως σκοπό τον έλεγχο του CRC στον αποδέκτη.
     * @param T η ακολουθία των n bits που πρέπει να ελεγχθεί.
     * @param P P ο προκαθορισμένος αριθμός με τον οποίο θα πρέπει να είναι διαιρέσιμη η ακολουθία Τ.
     * @return λογική τιμή που αντιπροσωπεύει την ορθότητα της ακολουθίας σύμφωνα με το CRC. true αν είναι ορθή, false διαφορετικά.
     */
    public static boolean checkCRC(long T, long P){
        // Απλά χρειάζεται να κάνω διαίρεση του T με τον P και να ελέγξω αν το υπόλοιπο είναι 0.
        return XORDivisionRemainder(T, P) == 0;
    }

    /**
     * Προσομοιώνει μετάδοση μηνύματος μέσω ενός ενθόρυβου καναλιού με Bit Error Rate BER και επιστρέφει
     * το "αλλοιωμένο" μήνυμα.
     * @param message το μήνυμα του οποίου η μετάδοση θα προσομοιώσει αποθηκευμένο σε (μεγάλο) ακέραιο.
     * @param messageLength το μήκος (αριθμό bits) του μηνύματος.
     * @param BER το Bit Error Rate του καναλιού.
     * @return το, πιθανώς, "αλλοιωμένο" μήνυμα.
     */
    public static long simulateTransmissionThroughNoisyChannel(long message, int messageLength, double BER){
        // Η γεννήτρια τυχαίων αριθμών για επιλογή του αν θα αλλοιωθεί το εκάστοτε bit.
        Random generator = new Random();

        // Δημιουργώ μεταβλητή που αποθηκεύει "δείκτη" στο bit του αριθμού message
        // που επεξεργάζομαι στο εκάστοτε βήμα.
        // Αρχικά, επεξεργάζομαι το 1ο bit (από τα δεξιά).
        long bitsIterator = 1;

        // Αποθηκεύω αριθμό bits-βημάτων που θα χρειάζεται να επεξεργαστώ.
        int bitsEnd = messageLength;

        // Επεξεργασία του κάθε bit του μηνύματος.
        for (int i = 0; i < bitsEnd; i++){
            if (generator.nextDouble() < BER){ // Αν "τύχει" να αλλοιωθεί το bit, το αλλάζω.
                // Η πράξη XOR με τον bitsIterator (που έχει 1 στο συγκεκριμένο bit που επεξεργαζόμαστε
                // συγκεκριμένο βήμα) ισοδυναμεί με αντιστροφή μόνο του εκάστοτε bit.
                message ^= bitsIterator;
            }

            // Shift του "δείκτη" προς το επόμενο bit για επεξεργασία.
            bitsIterator <<= 1;
        }

        return message;
    }

    /**
     * Εκτελεί και εμφανίζει τους ζητούμενους υπολογισμούς χρησιμοποιώντας τις παραπάνω μεθόδους.
     * @param args ορίσματα κονσόλας, δεν χρησιμοποιούνται.
     */
    public static void main(String[] args) {
        final int k = 20; // Μήκος (αριθμός bit) μηνυμάτων προς μετάδοση.
        // Ο δυαδικός αριθμός (μήκους n - k + 1, n αριθμός bit τελικού μηνύματος συμπεριλαμβανομένου του CRC) με τον οποίο θα πρέπει να διαιρείται
        // η τελική ακολουθία CRC (πρότυπο για τον υπολογισμό του CRC).
        final long P = Long.parseLong("110101", 2);
        final int PLength = Long.toBinaryString(P).length(); // Αριθμός bits του P (n-k+1).
        final double BER = Math.pow(10, -3); // Το δοθέν Bit Error Rate;

        int numberOfMessagesWithErrors = 0; // Μετρητής αριθμού μηνυμάτων που φτάνουν με σφάλμα στον αποδέκτη.
        // Μετρητής αριθμού μηνυμάτων που φτάνουν με σφάλμα στον αποδέκτη και εντοπίζονται ως εσφαλμένα από το CRC.
        int numberOfMessagesWithErrorsDetected = 0;

        final int numberOfMessages = 3000000; // Αριθμός μηνυμάτων που θα δοκιμαστούν.

        // Παραγωγή τυχαίων μηνυμάτων, υπολογισμό του CRC τους, μετάδοση τους μέσω ενθόρυβου καναλιού και έλεγχο
        // ορθότητας πραγματικό και από CRC.
        for (int i = 0; i < numberOfMessages; i++){
            long message = generateRandomMessage(k); // Δεδομένα προς μετάδοση.

            long messageT = calculateT(message, P); // Τελικό μήνυμα προς μετάδοση (μήνυμα ακολουθούμενο από το CRC).

            // Μετάδοση του μηνύματος και του CRC μέσω του ενθόρυβου καναλιού.
            long receivedMessage = simulateTransmissionThroughNoisyChannel(messageT, k + PLength, BER);

            // Έλεγχος αν το μήνυμα που έφτασε στον αποδέκτη ήταν εσφαλμένο, ανεξάρτητα του αν θα εντοπιστεί από τον CRC.
            if (receivedMessage != messageT){
                numberOfMessagesWithErrors++;
            }

            // Έλεγχος ορθότητας μέσω CRC.
            if (!checkCRC(receivedMessage, P)){
                numberOfMessagesWithErrorsDetected++;
            }
        }

        // Εμφάνιση αποτελεσμάτων.
        System.out.printf("Ποσοστό μηνυμάτων που φτάνουν με σφάλμα (στο block δεδομένων ή στο CRC) στον αποδέκτη: %f (%f%%)%n", (double) numberOfMessagesWithErrors/numberOfMessages,
                100.0 * numberOfMessagesWithErrors/numberOfMessages);
        System.out.printf("Ποσοστό μηνυμάτων που ανιχνεύονται ως εσφαλμένα από το CRC: %f (%f%%)%n", (double) numberOfMessagesWithErrorsDetected/numberOfMessages,
                100.0 * numberOfMessagesWithErrorsDetected/numberOfMessages);
        System.out.printf("Ποσοστό μηνυμάτων που φθάνουν με σφάλμα στον αποδέκτη και δεν ανιχνεύονται από το CRC: %f (%f%%)%n", (double) (numberOfMessagesWithErrors - numberOfMessagesWithErrorsDetected)/numberOfMessages,
                100.0 * (double)(numberOfMessagesWithErrors - numberOfMessagesWithErrorsDetected)/numberOfMessages);
    }
}

