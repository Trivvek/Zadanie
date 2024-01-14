import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

class ksiazka{
    String tytul;
    String autor;
    String isbn;
    boolean pozyczone;
    String imiepozyczajacego;
    Date datapozyczenia;
    Date datazwrocenia;

    public ksiazka(String tytul, String autor, String isbn){
        this.tytul = tytul;
        this.autor = autor;
        this.isbn = isbn;
        this.pozyczone = false;
    }
}

class biblioteka{
    List<ksiazka> ksiazki;

    public biblioteka(List<ksiazka> ksiazki){
        this.ksiazki = ksiazki;
    }

    public void szukajksiazek(String query){
        for (ksiazka ksiazka : ksiazki){
            if (ksiazka.tytul.contains(query) || ksiazka.autor.contains(query) || ksiazka.isbn.contains(query)){
                szczegolyksiazek(ksiazka);
            }
        }
    }

    public void pozyczksiazke(String isbn, String imiepozyczajacego, Date datazwrocenia){
        for (ksiazka ksiazka : ksiazki){
            if (ksiazka.isbn.equals(isbn) && !ksiazka.pozyczone){
                ksiazka.pozyczone = true;
                ksiazka.imiepozyczajacego = imiepozyczajacego;
                ksiazka.datapozyczenia = new Date();
                ksiazka.datazwrocenia = datazwrocenia;
                System.out.println("Książka wypożyczona pomyślnie.");
                return;
            }
        }
        System.out.println("Książka o podanym ISBN nie istnieje lub nie jest dostępna do wypożyczenia.");
    }

    public void pozyczoneksiazki(){
        for (ksiazka ksiazka : ksiazki){
            if (ksiazka.pozyczone){
                szczegolyksiazek(ksiazka);
            }
        }
    }

    public void ksiazpoterminie(){
        Date currentDate = new Date();
        for (ksiazka ksiazka : ksiazki){
            if (ksiazka.pozyczone && currentDate.after(ksiazka.datazwrocenia)){
                szczegolyksiazek(ksiazka);
                System.out.println("Data zwrotu przekroczona");
            }
        }
    }

    public void wszystkieksiazki(){
        for (ksiazka ksiazka : ksiazki){
            szczegolyksiazek(ksiazka);
        }
    }

    private void szczegolyksiazek(ksiazka ksiazka){
        System.out.println("Tytuł: " + ksiazka.tytul + ", Autor: " + ksiazka.autor + ", ISBN: " + ksiazka.isbn +
                ", Wypożyczona: " + (ksiazka.pozyczone ? "Tak" : "Nie"));
        if (ksiazka.pozyczone){
            System.out.println("Wypożyczający: " + ksiazka.imiepozyczajacego +
                    ", Data wypożyczenia: " + formatdaty(ksiazka.datapozyczenia) +
                    ", Data zwrotu: " + formatdaty(ksiazka.datazwrocenia));
        }
    }

    private String formatdaty(Date date){
        SimpleDateFormat formatdaty = new SimpleDateFormat("yyyy-MM-dd");
        return formatdaty.format(date);
    }
}

class Konta{
    private Map<String, String> users;

    public Konta(){
        users = new HashMap<>();
        users.put("admin", "admin");
    }

    public boolean Logowanie(String login, String haslo){
        return users.containsKey(login) && users.get(login).equals(haslo);
    }
}

public class Main{
    public static void main(String[] args){
        List<ksiazka> ksiazki = loadDataFromTextFile("books.txt");
        biblioteka biblioteka = new biblioteka(ksiazki);
        Konta Konta = new Konta();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Podaj login: ");
        String login = scanner.nextLine();
        System.out.print("Podaj hasło: ");
        String haslo = scanner.nextLine();

        if (Konta.Logowanie(login, haslo)){
            int wybor;
            do{
                System.out.println("\nMenu:");
                System.out.println("1. Wyszukaj książki");
                System.out.println("2. Wypożycz książkę");
                System.out.println("3. Wyświetl wypożyczone książki");
                System.out.println("4. Wyświetl książki po terminie");
                System.out.println("5. Wyświetl wszystkie książki");
                System.out.println("6. Wyjście");
                System.out.print("Wybierz opcję: ");
                wybor = scanner.nextInt();
                scanner.nextLine();

                switch (wybor){
                    case 1:
                        System.out.print("Podaj tytuł, autora lub ISBN książki: ");
                        String searchQuery = scanner.nextLine();
                        biblioteka.szukajksiazek(searchQuery);
                        break;
                    case 2:
                        System.out.print("Podaj ISBN książki do wypożyczenia: ");
                        String isbndopozyczenia = scanner.nextLine();
                        System.out.print("Podaj imię i nazwisko wypożyczającego: ");
                        String imiepozyczajacego = scanner.nextLine();
                        System.out.print("Podaj datę zwrotu (yyyy-MM-dd): ");
                        String zwrocdate = scanner.nextLine();
                        try {
                            Date datazwrocenia = new SimpleDateFormat("yyyy-MM-dd").parse(zwrocdate);
                            biblioteka.pozyczksiazke(isbndopozyczenia, imiepozyczajacego, datazwrocenia);
                        } catch (Exception e) {
                            System.out.println("Błąd podczas parsowania daty.");
                        }
                        break;
                    case 3:
                        biblioteka.pozyczoneksiazki();
                        break;
                    case 4:
                        biblioteka.ksiazpoterminie();
                        break;
                    case 5:
                        biblioteka.wszystkieksiazki();
                        break;
                    case 6:
                        System.out.println("Zamykanie systemu");
                        break;
                    default:
                        System.out.println("Nieprawidłowy wybór, spróbuj ponownie.");
                }

            } while (wybor != 6);
        } else{
            System.out.println("Błędny login lub hasło. Aplikacja zostanie zamknięta.");
        }
    }

    private static List<ksiazka> loadDataFromTextFile(String filePath){
        List<ksiazka> ksiazki = new ArrayList<>();
        try{
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3){
                    String tytul = parts[0].trim();
                    String autor = parts[1].trim();
                    String isbn = parts[2].trim();
                    ksiazki.add(new ksiazka(tytul, autor, isbn));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return ksiazki;
    }
}