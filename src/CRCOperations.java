import java.util.Random;

/**
 * Η κλάση στην οποία υπάρχουν οι μέθοδοι οι οποίες υλοποιούν τις απαιτήσεις της εργασίας.
 */
public class CRCOperations {

    /**
     * Δημιουργεί τυχαία δυαδικά μηνύματα των k bits (μπλοκ δεδομένων των k bits, σε κάθε bit των
     * οποίων, το 0 και το 1 έχουν ίση πιθανότητα εμφάνισης).
     * @param k αριθμός bit δυαδικού μηνύματος.
     * @return τυχαίο δυαδικό μήνυμα k των bits.
     */
    public static long generateRandomMessage(int k){
        // Η γεννήτρια τυχαίων αριθμών για επιλογή του κάθε bit.
        Random generator = new Random();

        // Το τυχαίο μήνυμα ξεκινάει με 0 και τα ψηφία του θα προστεθούν ένα-ένα, σταδιακά.
        long result = 0;

        for (; k > 0; k--){
            // Shift αριθμού αριστερά κατά μία θέση για αργότερη προσθήκη νέου bit.
            result <<= 1;
            // Επιλογή bit 1:
            if (generator.nextDouble() > 0.5){
                // "Προσθήκη" 1 στο τέλος του αριθμού.
                result |= 1;
            }

            // Επιλογή bit 0:
            // Αν δεν ισχύει η συνθήκη του if, δεν αλλάζω κάτι στον αριθμό, νοητή "προσθήκη" 0 στο τέλος του.
        }

        return result;
    }

    /**
     * Υλοποιεί διαίρεση modulo-2 αριθμητικής των δύο αριθμών και επιστρέφει το υπόλοιπο.
     * @param dividend διαιρετέος.
     * @param divisor διαιρέτης.
     * @return υπόλοιπο modulo-2 διαίρεσης.
     */
    private static long XORDivisionRemainder(long dividend, long divisor){
        // Μετατροπή του διαιρετέου σε bits ώστε αργότερα να μπορώ να τα "κατεβάζω" ένα-ένα.
        String dividendBits = Long.toBinaryString(dividend);

        // Μετατροπή του διαιρέτη σε bits.
        int divisorBitsLength = Long.toBinaryString(divisor).length();

        // Αποθηκεύω την θέση του πιο αριστερού bit του διαιρέτη ώστε αργότερα να μπορώ να
        // εξασφαλίσω ότι οι αριθμοί στους οποίους κάνω XOR είναι ευθυγραμμισμένοι.
        long divisorLeftmostBit = 1L << divisorBitsLength - 1;

        // Διαχείριση περίπτωσης όπου πλήθος bits διαιρέτη > πλήθος bits διαιρετέου.
        if (dividendBits.length() < divisorBitsLength){
            return dividend;
        }

        // Δείκτης θέσης bit διαιρέτη που είναι επόμενο για να "κατέβει".
        int dividendBitsIterator = divisorBitsLength;

        // Shift του διαιρετέου προς τα δεξιά ώστε τα bits διαιρέτη και διαιρετέου να ευθυγραμμιστούν.
        // Χρήση της Math.max() για διασφάλιση σωστής λειτουργίας ακόμη και σε περίπτωση που bits διαιρέτη > bits διαιρετέου.
        long remainder = dividend >> dividendBits.length() - divisorBitsLength;

        // Όσο υπάρχουν ψηφία (bits) που δεν έχουν ακόμα "κατέβει" συνεχίζω τις πράξεις XOR.
        while (dividendBitsIterator < dividendBits.length()){
            // Πράξη XOR μεταξύ επιμέρους ποσοτήτων. Ανάλογη (της modulo 2 διαίρεσης) πράξη με την αφαίρεση
            // στην συνηθισμένη διαίρεση.
            remainder ^= divisor;

            // Όσο διαιρέτης και τρέχον υπόλοιπο δεν είναι ευθυγραμμισμένα, "κατεβάζω" ψηφία απο τον διαιρετέο.
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

        // Έχουν τελειώσει διαθέσιμα ψηφία που μπορούν να κατέβουν και αυτό που απομένει είναι το υπόλοιπο.
        return remainder;
    }

    /**
     * Υπολογίζει και επιστρέφει τον κώδικα CRC του δοθέντος μηνύματος (ακολουθία δεδομένων προς μετάδοση).
     * @param D η προς μετάδοση ακολουθία δεδομένων των k bits αποθηκευμένη σε (μεγάλο) ακέραιο.
     * @param P ο προκαθορισμένος αριθμός των n-k+1 bits με τον οποίο θα πρέπει να είναι διαιρέσιμη η ακολουθία Τ.
     * @return τον κώδικα CRC του δοθέντος μηνύματος.
     */
    public static long calculateCRC(long D, long P){
        // Αποθηκεύω την τιμή 2^(n-k)*D που θα χρειαστεί στην παραγωγή του FCS και τελικά του CRC.
        long DShifted = D << Long.toBinaryString(P).length() - 1;

        // Δημιουργώ την ακολουθία FCS.
        // Η μεταβλητή DShifted είναι ο D στον οποίο έχουν τοποθετηθεί n-k (πλήθος bits P - 1 = n-k+1-1 = n - k) μηδενικά.
        // Η ακολουθία FCS είναι το υπόλοιπο της διαίρεσης (modulo-2) του DShifted με το P.
        long FCS = XORDivisionRemainder(DShifted, P);

        // Η ακολουθία CRC παράγεται με την πράξη 2^(n-k)*D + FCS.
        return DShifted + FCS;
    }

    /**
     * Ελέγχει την εγκυρότητα της ακολουθίας CRC με βάση τον αριθμό P.
     * Έχει ως σκοπό τον έλεγχο του CRC στον αποδέκτη.
     * @param T η ακολουθία CRC.
     * @param P P ο προκαθορισμένος αριθμός με τον οποίο θα πρέπει να είναι διαιρέσιμη η ακολουθία Τ.
     * @return λογική τιμή που αντιπροσωπεύει την ορθότητα της ακολουθίας CRC. true αν είναι ορθή, false διαφορετικά.
     */
    public static boolean checkCRC(long T, long P){
        // Απλά χρειάζεται να κάνω διαίρεση του T με τον P και να ελέγξω αν το υπόλοιπο είναι 0.
        return XORDivisionRemainder(T, P) == 0;
    }

    /**
     * Προσομοιώνει μετάδοση μηνύματος μέσω ενός ενθόρυβου καναλιού με Bit Error Rate BER και επιστρέφει
     * το "αλλοιωμένου" μήνυμα.
     * @param message το μήνυμα του οποίου η μετάδοση θα προσομοιώσει αποθηκευμένο σε (μεγάλο) ακέραιο.
     * @param BER το Bit Error Rate του καναλιού.
     * @return το, πιθανώς, "αλλοιωμένο" μήνυμα.
     */
    public static long simulateTransmissionThroughNoisyChannel(long message, double BER){
        // Η γεννήτρια τυχαίων αριθμών για επιλογή του αν θα αλλοιωθεί το εκάστοτε bit.
        Random generator = new Random();

        // Δημιουργώ μεταβλητή που αποθηκεύει "δείκτη" στο bit του αριθμού message
        // που επεξεργάζομαι στο εκάστοτε βήμα.
        long bitsIterator = 1;

        // Αποθηκεύω αριθμό bits-βημάτων που θα χρειάζεται να επεξεργαστώ.
        int bitsEnd = Long.toBinaryString(message).length();

        // Επεξεργασία του κάθε bit του μηνύματος.
        for (int i = 0; i < bitsEnd; i++){
            if (generator.nextDouble() < BER){ // Αν επιλεγεί να αλλοιωθεί το bit, το αλλάζω.
                // Η πράξη XOR με τον bitsIterator (που έχει 1 στο συγκεκριμένο bit που επεξεργαζόμαστε
                // συγκεκριμένο βήμα) ισοδυναμεί με αντιστροφή μόνο του εκάστοτε bit.
                message ^= bitsIterator;
            }

            // Shift του "δείκτη" προς το επόμενο bit για επεξεργασία.
            bitsIterator <<= 1;
        }

        return message;
    }

    public static void main(String[] args) {
        final int k = 20; // Μήκος (αριθμός bit) μηνυμάτων προς μετάδοση.
        // Ο ακέραιος (μήκους n - k + 1, n αριθμός bit τελικού μηνύματος με CRC) με τον οποίο θα πρέπει να διαιρείται
        // η τελική ακολουθία CRC.
        final long P = Long.parseLong("0110101", 2);
        final double BER = Math.pow(10, -3); // Το δοθέν Bit Error Rate;

        int numberOfMessagesWithErrors = 0; // Μετρητής αριθμού μηνυμάτων που φτάνουν με σφάλμα στον αποδέκτη.
        // Μετρητής αριθμού μηνυμάτων που φτάνουν με σφάλμα στον αποδέκτη και εντοπίζονται ως εσφαλμένα απο το CRC.
        int numberOfMessagesWithErrorsDetected = 0;

        final int numberOfMessages = 3000000; // Αριθμός μηνυμάτων που θα δοκιμαστούν.

        for (int i = 0; i < numberOfMessages; i++){
            long message = generateRandomMessage(k); // Δεδομένα προς μετάδοση.

            long messageCRC = calculateCRC(message, P); // CRC του μηνύματος προς μετάδοση.

            // Μετάδοση του μηνύματος και του CRC μέσω του ενθόρυβου καναλιού.
            long receivedMessage = simulateTransmissionThroughNoisyChannel(messageCRC, BER);

            // Έλεγχος αν το μήνυμα που έφτασε στον αποδέκτη ήταν εσφαλμένο.
            if (receivedMessage != messageCRC){
                numberOfMessagesWithErrors++;
            }

            // Έλεγχος ορθότητας μέσω CRC.
            if (!checkCRC(receivedMessage, P)){
                numberOfMessagesWithErrorsDetected++;
            }
        }

        // Εμφάνιση αποτελεσμάτων.
        System.out.printf("Ποσοστό μηνυμάτων που φτάνουν με σφάλμα στον δέκτη: %f (%f%%)%n", (float) numberOfMessagesWithErrors/numberOfMessages,
                100.0 * numberOfMessagesWithErrors/numberOfMessages);
        System.out.printf("Ποσοστό μηνυμάτων που ανιχνεύονται ως εσφαλμένα από το CRC: %f (%f%%)%n", (float) numberOfMessagesWithErrorsDetected/numberOfMessagesWithErrors,
                100.0 * numberOfMessagesWithErrorsDetected/numberOfMessagesWithErrors);
        System.out.printf("Ποσοστό μηνυμάτων που φθάνουν με σφάλμα στον αποδέκτη και δεν ανιχνεύονται από το CRC: %f (%f%%)%n", 1 - (float) numberOfMessagesWithErrorsDetected/numberOfMessagesWithErrors,
                100.0 * (1 - (float)numberOfMessagesWithErrorsDetected/numberOfMessagesWithErrors));
    }
}
