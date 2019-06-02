# MovieWishlist

Aplikace pro ukládání seznamu filmů k pozdějšímu zhlédnutí.

### ToDo:
 
* [x] Záznamy o filmech v databázi
* [x] Rozdělení do kategorií    
* [x] Diakritika
    * [x] Lokalizace
* [x] Filtrování skrze search bar
* [x] Vyhledávat z IMDb
    * [x] Jednoduchý test
    * [x] Přidat zvolením nalezeného filmu, popř. kliknutím nahoře, když se nic nenajde
    * [x] ```movie``` v API
    * [x] Zavřít AlertDialog po přidání filmu
* [x] Odstranit splash screen
    * [x] Přeskočit ```MainActivity``` při zapnutí i cestě zpět
* [x] Víc barev 
    * [x] Jiná barva toolbaru
    * [x] Kontrastní barvy
    * [x] Bílý/černý text v toolbaru
* [x] Oficiální Android ikony
    * [x] Menší
    * [x] Zarovnané na střed
* [x] Refaktorovat vše do vlastních tříd
* [x] Neobnobovat ItemAdapter při každém spuštění
* [ ] Vyřešit *Watch* a *Reset* pro kategorii
    * [ ] Smazat zhlédnuté
    * [x] Přeškrtnou, zašedit zhlédnuté
    * [ ] Posunout je na konec
* [x] Ukládat pořadí drag & drop do databáze
* [x] Výchozí hodnoty v databázi při prvním spuštění aplikace

## Check

* [x] *Nemuzu napsat ěšč do edittextu pro vyhledani filmu*
  * V emulátoru mi to taky nejde (nevím proč), ale v mobilu na živo už ano
* [x] *Vyhledavani z IMDB už během psaní*
* [x] *Pri otoceni zarizeni zmizi vsechny zobrazene dialogy!*
* [x] *Pri otoceni zarizeni se mi odstrani text, ktery jsem zadal do vyhledavaciho filtru!*
* [x] *Spolecna metoda pro dialogy*
* [x] *Add or update jednou metodou*
* *Padding*
  * [x] Pravý a levý pro checkbox a tlačítka
  * [x] Vystředit checkbox a tlačítka
  * [x] Category dialog EditText
* *Smazat nepoužité třídy*
  * [x] ```MainActivity```
  * [x] ```IMDb```
* ```data class```
  * [x] ```Category```
  * [x] ```MovieItem```
* [x] *Smazat prefixy layoutů*
* [x] *Zjednodušený* ```setNegativeButton```