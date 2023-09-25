# Demo project for DENEVY

## Assignment description
> Junior - spring rest aplikace v kotlinu Rezervace zasedacich mistnosti
> - 3 endpointy GET, PUT, POST, DELETE
> - simulace zasedacich mistnosti
> 
> Zadani:
> Mam 4 zasedaci mistnosti, dane mistnosti mohu rezervovat ale zaroven si zrusit rezervaci. Dane rezervace jsou vazane
> casem a tedy ve stejny cas nemohou rezervovat mistost 2 pracovnici.
> Podminky:
> - mistnosti lze rezervovat od 8:00 do 17:00
> - mistnost muze rezervovat pouze 1 clovek v urcity cas
> 
> Zakladni parametry funkce GET
> - jmeno rezervujiciho
> - prijmeni rezervujiciho
> - cas rezervovani
> - id rezervace
> mistnost k rezervovani
> Splnene zadani
> - plne bezici aplikace s navrhem dane architektury vcetne funkcnich endpointu
>   - uprava rezervaci, vkladani rezervaci a vytazeni rezervaci viz parametry funkce GET
> - idealnim pripade vcetne logiky na napojni postgres DB ale muze byt pouze v ramci datove struktry v APP

> 
> Senior - spring REST aplikace v kotlinu Rezervace zasedacich mistnosti
> 
> Zadani:
> Vytvoreni Spring aplikace napojenou na postgress DB, vystaveni endpointu pro moznost rezervace, zruseni rezervace, upravu mistnosti, …
> 
> Podminky:
> - mistnosti lze rezervovat od 8:00 do 17:00
> - mistnost muze rezervovat pouze 1 clovek v urcity cas
> - v mistnosti je technika (projektor, vyukovy PC, chytra tabule) ktera se muze prenaset v ramci mistnosti
> - v ramci rezervace mistnosti muze prijit pozadavek na techniku a program musi vyhodnotit zda technika v dane mistnosti je pripadne zda nemuze byt
> premistnena z jine mistnosti. Samozrejme v tomto pripade pokud je technika premistnena,
> nelze danou mistnost odkuj je vzata rezervovat s pozadavkem na danou techniku.
> 
> Splnene zadani

## Application run
Standard SpringBoot application running/listening on port 8080 for incoming request. Needs PostgresSQL database for its run to store/read static data, persists data about bookings in database.

Prerequisites:
- checked out project
- maven
- docker

To run the application use IDE's standard functionality or issue following command in project folder:
`mvn spring-boot:run`

PostgresSQL database docker image is bootstrapped automatically on application startup due to presence of `docker-compose.yml` file. This file also contains connection details to database (username, password, port).

## API documentation and testing
Requests can be sent towards the application from published [swagger](http://127.0.0.1:8080/swagger-ui/index.html) page, or using preferred tool, i.e. Postman.
