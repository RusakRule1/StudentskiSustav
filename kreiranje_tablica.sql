DROP TABLE IF EXISTS korisnik CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS profesor CASCADE;
DROP TABLE IF EXISTS admin CASCADE;
DROP TABLE IF EXISTS predmet CASCADE;
DROP TABLE IF EXISTS upis CASCADE;
DROP TABLE IF EXISTS zadatak CASCADE;
DROP TABLE IF EXISTS predaja_zadatka CASCADE;
DROP TABLE IF EXISTS ocjena CASCADE;

CREATE TABLE IF NOT EXISTS korisnik (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    lozinka_hash VARCHAR(255) NOT NULL,
    sifrirano_ime VARCHAR(500) NOT NULL,
    sifrirano_prezime VARCHAR(500) NOT NULL,
    uloga VARCHAR(50) NOT NULL CHECK (uloga IN ('STUDENT', 'PROFESOR', 'ADMIN'))
    );

CREATE TABLE IF NOT EXISTS student (
    korisnik_id INT PRIMARY KEY,
    jmbag VARCHAR(10) UNIQUE NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS profesor (
    korisnik_id INT PRIMARY KEY,
    titula VARCHAR(50) NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS admin (
    korisnik_id INT PRIMARY KEY,
    ovlasti TEXT NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS predmet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    sifra VARCHAR(10) NOT NULL,
    ects_bodovi INT NOT NULL CHECK (ects_bodovi > 0 AND ects_bodovi <= 30),
    profesor_id INT,
    semestar VARCHAR(20) NOT NULL CHECK (semestar IN ('ZIMSKI', 'LJETNI')),
    godina_izvodenja INT NOT NULL CHECK (godina_izvodenja BETWEEN 1 AND 5),
    FOREIGN KEY (profesor_id) REFERENCES profesor(korisnik_id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS upis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    predmet_id INT NOT NULL,
    datum_upisa TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    polozen BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT unique_student_predmet UNIQUE (student_id, predmet_id),
    FOREIGN KEY (student_id) REFERENCES student(korisnik_id) ON DELETE CASCADE,
    FOREIGN KEY (predmet_id) REFERENCES predmet(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS zadatak (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT NOT NULL,
    predmet_id INT NOT NULL,
    rok_predaje TIMESTAMP NOT NULL,
    datum_objave TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (predmet_id) REFERENCES predmet(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS predaja_zadatka (
    id INT AUTO_INCREMENT PRIMARY KEY,
    zadatak_id INT NOT NULL,
    student_id INT NOT NULL,
    naziv_datoteke VARCHAR(255) NOT NULL,
    tip_datoteke VARCHAR(50) NOT NULL,
    velicina_datoteke BIGINT NOT NULL,
    predana_datoteka BLOB NOT NULL,
    datum_predaje TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PREDANO' CHECK (status IN ('PREDANO', 'OCJENJENO')),
    CONSTRAINT unique_zadatak_student UNIQUE (zadatak_id, student_id),
    FOREIGN KEY (zadatak_id) REFERENCES zadatak(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(korisnik_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS ocjena (
    id INT AUTO_INCREMENT PRIMARY KEY,
    predaja_id INT NOT NULL UNIQUE,
    profesor_id INT NOT NULL,
    vrijednost INT NOT NULL CHECK (vrijednost BETWEEN 1 AND 5),
    komentar TEXT NOT NULL,
    datum_ocjenjivanja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (predaja_id) REFERENCES predaja_zadatka(id) ON DELETE CASCADE,
    FOREIGN KEY (profesor_id) REFERENCES profesor(korisnik_id) ON DELETE CASCADE
    );

INSERT INTO korisnik (email, lozinka_hash, sifrirano_ime, sifrirano_prezime, uloga) VALUES
('ivan.horvat@student.unizg.hr', 'e305fa8a2e52af3386d706460a85c8bdf0eabd91266c3379a39e45b53a27d7bb', 'XTOVnUG6w3NkYrCltqCVYw==', 'G9eYwUFGDgIOoiSHaiBWqzF8QK103tkmC2L6W1NSmUVCCA9KVWhw8MWfPO3b+3vCZhCErV19huyFQrjRRUWEWSDU0zlIT3WpYaQ6Ch7otJ2/1mPgdL9XHfTuyrSyoB53Icuk+WNa/FlcgVnSz3F7AqrS9MfQi0tDv8SZNsdncTHraxmDPsthO4YCe6Gx2EhdKcZSdo/uQiSyFOoIRn/g64XkJAMUL4ueofZYv5naN1W7vmodCmjyIOFgfjX6C5TAllp8ATOSlY0IwOsAvQiBuc5Qigz9p6hN58BH6hbcGVGpxGuyr7Qjue2A/JOgcv+/RLjbp0ClCxfpAP+uEwhDcg==', 'STUDENT'),
('ana.kovacevic@student.unizg.hr', 'fa462c1a7ffe8d7b71fa47592cd0b75aa166aa347e6810f373c11b6ea0e127bb', 'lLC3Ht6H9QeNZ0tVXcX5dw==', 'bp19MPMKjbjZerKYDRqC3Z8IJF0Gm722OAG8OPPHRIw3a1XorlwNQAqjw6aDVwiyHGF3k4UYK117AyfdVYXM2Y4JZhz93Hp5ah8cIA1Ko3Exx/N/qEhcEnTXQggMtHxjjMH4+EPldys36Ti3zS59QXVwz/byNej17f/K2BKJgkJxy3QI7vq79z6SXoXv0G/QZ/sBhITdc04TWQBiRuTFMskOPVRB9KTKEKnjv4wZxxEyGzbw2JgemjkJZ+sKoSvl8pEK7sBM4ZYRvN+/w89C+akCtK+JkKi+ehC6pjcavAIrY6OYholnNLlPKRJcUCsE38k/YEtyHV5OdI4Ey1B7Bw==', 'STUDENT'),
('marko.babic@student.unizg.hr', 'a4ba1734ec346bcaf23c9f005e038d9e83126d332b2fbce7fb3afbd44acb0780', 'xLBjgpbPSWrnURG5ZTt24w==', 'Fr4GbQaQlQDnFBokl4hAA+e9vUDj0TdY1NAJin9e9bbojUkZ6kxSERc+2L5nZcRK+bpOcYKVmec/0e4pERVDXpBLdcL5GOmsI2RJCeBNxKuwnV54S1SykIi0KoHJTm5iS44+4ScoyUkr+o9kwnL3Brz+/MxOVB8L6cL/2+OS4xL5o04fNgA+uub/xjanj8f7Nm8F7IZrur34lW3+Bfw4fqoKsc9766xGflk8bstbt4k8KuKxq6rWOGVjScHuMJKkAOEqfT5kMVKrXuuQT5uSMQPSOA7E7TrDi3FgO/8v3S0ywlJzqzD67avqU9beIvykbeH2WVOxdXHLV7fGAJkCmw==', 'STUDENT'),
('petra.novak@student.unizg.hr', 'e23c90fe8f74999f03a1f3a31fcb2c76a72f42768f2ff2c53dbc197fd71095ab', 'UWXIQav8BywHRAwb0rbCTQ==', 'UY0QqGtX9V9REHvSsBJtasX+25DA0zt8gOHvmUjigHbYGPJgneAzNtpoDrf6KcZspkstSX7mEJDR93v+niDfdhcSuiv3rhdqKXNbgCBn33lgpjuKApzIRqJxJwzK7MUWvEygoi+SQ4hPU5ri/UqWVBt7u5yBF3gAe5BlS2GDemC+HpZYRfYctB9eVXeenrXoBH3QxYfH0cKWm2kD8S645oH+esQVDKjIunNN2v55NU2xshigqb0h0cL24D+3GQpA5J3BbWHYwmfXCQZRAe/j00AeTU/cO49D9IgXaeutxskCLA3msBlnrhxZqziL2Vr1fCi1HWpHL/cxPDnASHguXg==', 'STUDENT'),
('luka.juric@student.unizg.hr', 'd91841c212958842c832e6b4009aedb950815ba721abdc07bc93563664183290', 'dJUoCGBT7UNWtufLI8NFMA==', 'GS4tf5k+8oWzg2OBcNBwVNaiBlUzcohQ5ElX1oo5pbKUIex32qBRA71XQyAKC0olfXDdGOx61cJ9MUAwy7M+JtxIHDhsQFS2lc3EJ9/j5CI2Png05XfJNu7z8G98wxlXSNIIRRGTKAtHkK7kifFo9xpnqv2sBHNKZD4xqd2xP0D1WEP5vZrcWQUjNX9z7jRGnrieBuIkWyDsVtmvhkvObI4Dghl1tNVVkaBPxElBYedHpL/f8K9GbLqkEyfpGeUlqwkwpsDLoZB9gKmQwKHNIpd/w7/GxWtV9KBf/4qxp0xLBaXbgu2FY42ImX2DKx2sny0hLP81YRwK7B8N6bkWGQ==', 'STUDENT'),
('maja.pavic@student.unizg.hr', 'a7fa80fd739d6b2b3deb80c2bcc41d2dcc8142c022a969a720eef2936b3e438f', '5H0ao/gMm6+I3uQlPtryuw==', 'P0hQEV68iDJQnlOwbvWWBDDqJz2/R6gQOD0NtmX68aXzwtQSlbAS8fxlKnIBs12bcGsXc6Rapj3EF2bhcg0rD/2W7dPyHdGGW5vIDZFy3gGn4/xf0zWswadK75jL2S9+EQpAJHwZvE7lig55/RGHU9LsyhVIT369FH5E3j+4fYcFjQka4WWyKQU4ecXML19PtcaCKvm35R83u7zkoG/1AyLpVGsW7Hmh+VzNTE8Gti2vEX9gRL8pKCGw6ua7l7s1i+1HyVkZUraqTtMJm7Ff0kdNWam1yj/eL7RT69OMzRWb93ubvAgafDGdM6wFf7oxUvYniH87fYIXCAW7QcdeFQ==', 'STUDENT'),
('tomislav.maric@student.unizg.hr', 'ad7228a51643af901c4311745c41417ef240439851b67a54768c26086e923fc9', 'GPd+/ZMqhn13Vf2/hl7HhQ==', 'TW5otxaWFEqeJQqcUqWIY2kip5GqlYWnnOOEXbdyvS7/gSBiA1xJOLux7CZFpXRonaBWKAhq+tI8TPOh26Fm+hf9tmMbUNiHH/bkDqz/e5x05qdqrEaK3Hm3Qk3ECJRfWN57/wVaz+xFHunh/54gApQ9oaX/U1ReVcr7Wssa42ktK+opxDNloOxZNtJDZv5+wWy41BkUxBrvIQsSL7Ed5s98sPKKtS7yV0C4W8RIb+AxMU1lbnWo1qOLDxHDDW0JeQFJrMpfkBZ5PI8ur0Z9p24NqSFIbDtAM77N4B3RTKBUuQ1spWSrhP/f5ePvKwe6F1bECVV0KOPNCWLmQf9E8Q==', 'STUDENT'),
('sara.knezevic@student.unizg.hr', 'a0c6c03a0eddd5129cea0bd6d3f3682fe8f1e509ac3d8676a5fdc816e907f414', 'thTv6hCjb9JbRkIN55jIkw==', 'PIrUxlqZMvUkrSscRafGMJDDe1LdnPh6dSKyrHklD5HqqzVLKk120M1yU5++DsvIrm4MyGuKpjNUP91CYIQqyIC5twVHBHPWB4g1VLwf/OhV7gUq7VOuOj4LwJYS6mf03zFvcPHV41pQtcvqr+GOsRng5K805SvWsTsdiGkdqr1GsLnbz47GUvxzzD2pNeZYuMvdolAf9JbAsbGd5D7U8iKDT1zsdd4bg4cbZ+qlX4KF4QHoulB+KgzI/3jqSajK4J9P58FAR2P2Yx+y3Iqj+0PndNTZKT8E4LaZfoGmy92JXyZmUCzoOlRN+jKgG6yo3d9w8OV7Enfg4n/Dyn953w==', 'STUDENT'),
('david.bozic@student.unizg.hr', 'e12329eaa58798dfa96d220870a0f34d8e7f6f490992da0dabb841dbd2b7f504', 'Izp3fXcFDAiOWfUrzWMR8A==', 'KnMXpC/18ymEJSpAKuYCjcTJDZoXvi/J+aB6SBJfoJKMtljqXoAEYtN1OOumaQpCxAQ7W5KKf/Cg42JUMIOxDz7cd07/1MYd3rNKQcZCUJ58BtU3L8D/rPkNUTwNBqYaarkA7jSgNnc0ZP5aklIstqa2GYyc1w1ucu4jqXW5Vbng7cPv0mV4uy2+feHCoe2z0BtaJ8IkQlShwhX2PybRifeRCtjWzxx0ssAVq0Cmu5PzHuj0RQFYQ7EWurULC2xIvgKwLGpfmOwG7jsinMj1uLk8hnCZykJHm9QargP5/a5gYVqSpxs8+bGkCzhwp7f93BohpP7uQmNaRTYaEzwz3w==', 'STUDENT'),
('karla.varga@student.unizg.hr', 'f242778d0e117d7f7f74075db180cfac8cd4d126ae9d6980d942e441d46feece', 'NUOjkKxy9taVP8ibnarnXQ==', 'DVSpTfrzM9xySCp7aEQqryFWEP3SHdUIPhiaYp4WWq40NcSBZOR2UtG5LOZIAl9xvtjYgZUp8U1sl/fAMqieYJJahADuBT+jcOUs9MnL6jWevgdJycaoWBooXGzf46VGZ+qgeMJz2DcfjRkHhuq6PiXUFFBhno6DJTp17OVcRsRsNL4Rr6JzvupTRqgBe0pGZhQGyF3qEm3hIM3/J/gXmHhkjBrJvOGffGmJuQUHE3YfAfWPXC/S9vR3VnctFASveFxro+85w1re0vpKXZtEP4df2/vxyvNPkagvu4LYxDf2KZr9zb+Y2HXKsfg9d8CbF9XG8DzDEdwedQ3NES4RFA==', 'STUDENT'),
('mateo.zupan@student.unizg.hr', 'c46ed2aaaa3a1af7c429d0b0d52bac54195bf363ab99ed17e1364d218f7be04a', 'Dmf9aKP5iPrwdYS/6sHXgw==', 'ebImTcifVEetHtQAmSUAkCfgazZf9mQT9iOyC9+w4Lt9VbzQfLJCMN7WA1piu5zx4fuu+jXLZUNtkIVHBrQt/MTUAYOZ2RJQCoGJflR/vgndGg3kRKl7KtUhFfxy5Okh6zor8ysmmBa05IRVAOuC3DKDv4NhKTamKxl3ULM3weSM5Un6y+bta1fWkE7EJBELye4c4H5w6gxV+UEHWN1yKaYAxtr7XOLVzllyjXlUggiaqm92Gkx+siCeWpwc2HuOg8kaBPjOr5KWmQo5dHGIMoWlomGLjuFo33QVsobeYCN7KRAk/ARlHh8Ta2jGdIkRwTP7ThnU6lMKegu9LkpP5A==', 'STUDENT'),
('lena.kralj@student.unizg.hr', 'cd99a4bcd7cdc7c10203767061fb82a8943d4ff9c376d71cb1a6a39aae543b9a', 'IW6XIoPVEXLMYkNUD4Mknw==', 'YliFArAA1fOJ9A8ADzO5tG3/W5cgHr+JAWxhR5NICepJary5jhrmrIqiKx4JlwAJVMAUS5qZbhzAZIi4lCLaX7QPtxf0J/TcUdgoAWxBa6WlOhQPwLmG01DKPnt9eiY/QHbA3syi3aqyI5ZgKP5YhF3NO8FWIoVb00gNCuaFm6a0SpytRiZEdvCOn1FbGfKiRorekmWWYPOR2JefquuQCu3yGyyGZhFuJ35Y+9xy/Zv2EZ8qPA+zI/phV7DE+GAFGOSRw4ue45BF7H0a7qSunn5UaiEr0hJXUZkJ7UuNe2B+xQ4w3vKY7QvnpJV53G6T+sn0nXTmkkkSUr0Pz3rnkg==', 'STUDENT'),
('lovro.simic@student.unizg.hr', 'd4ddd7216b3dc976cffc5f749a24e44431de78d02eba44de75931d0c7bf97a47', '2lk3As28nKnXiUzdGZeyAg==', 'f4RiNewwEzaNNzNq15Ntiwn30JzK3gOlcEhMIwQw+lKvknguwiZNnXPYvFarWQEvzXOYf2ZSkZhHA/GuGGziNC4xTDejUHX0R+69CNczH5TxWt3aPtRmppg7Os94PMky+dtrAnON9yryB4XQhE7cTCajLHUs7LQ9swXLnKw/wDtjux2ZQuc6vLqr3ajLooz2O982/JAP5zcER9AV1crEVgOSL9+T9bt62oVXdmt6Y97qJzXjRHzz7Tkda7eXU2V1XRcqbhPKeNiDqqsKm+Kga00NqWtRbFapCPnSVJ5hWsQpGe9rfPlSQr5gcznGhu2iHaGKzN61bjJ3dAGrItlTTg==', 'STUDENT'),
('ella.milakovic@student.unizg.hr', 'b76f90ac26ef51d1c6e0ccfd7857128554bae88fbc380e6d0b08dcbbe427f72b', 'qke30bQMyo/YzKTMicK0TQ==', 'fYCCDUpaC33I3xJ7e9/EqI0iSB5wLef+LydyP/8Y+PU6/Z4U+PeDawkcBnLKpHIWabk+WxmMeghugX4GGWTxVXSbi3miU/Uqp8C7aDCtl6IUqgNepp14o6aN+1/vDQuIIStldGxrtc6s9C8G++HbxfvficEiBW17LYmOswVRDLtlShKJzyNrIllTB19GfqkeqrgJUfIQZd7ELpz3rKu3kVMWLM/p1AADeIPdDKMsxWZ5sXEPEWGpIpIAQ8AuV9DTYNydCO6kSialZGEkMiJrjtK27T7epaZ6kzPj0nJV82If1UU6TLO17834sYRktxS6KuSS/N0/xDpNhjyFhMJ9QA==', 'STUDENT'),
('leo.tomic@student.unizg.hr', 'cc980f200ce3b8b3b79eea464b9596cc6874e0e5f21278cda4d5dd75253fef98', 'EsHXuPPRTAAE7fk4Vf36qw==', 'Kofiqh+0RQhjciutcRWeGsXc5fOzJopcPOXvDk2o4P/aSzuf5J8vu5GmUWm1Mmcvsb53wugiiphpD1FIUguSE0SR4ejeEQQDlANHTVfo6vjT99UU4kKTlZFqkadQTNZ/ZdDmEe6LteOX4/zsn25eGywRgQbPuSggTGbc3JiSvLNmubq+CmzxBE0lEshFc8bnE2+d7aSJoMYgqF8fnvajlViQB/n2d3mc8CtUVteOvqqRjewgi5pyEimipgUMsPZ0yzPh8S8dPyHyeXwr6oFE03NOO4zuEqb1RpDJjY/bxwDvymy6vOU8inH1MJ+hnPK2BjLje1aNktysTtrFAHlh2g==', 'STUDENT'),
('nina.grgic@student.unizg.hr', 'f1a196bb59d0205f167d98792476e933bd31b9c47950dccbce708bb432823318', 'fpyNnhIygBfiNTbq1RxPlQ==', 'hJgz2Y0iABWg0SyJaXZyYTcE0mvyrt+ie2FNOu2z/bH4JnS/SpK/HshSpJYXTXY7eZx0HQVgxNKU1YMLjqzFOUUIokK1KsUSZBIX/eKfdtqYg8Jt35sQmZ3cXZXIxzBmacv4uN5Qw6H5DuBLVthey5vXmURmCuiQR2ESMjbsAMVVM9wF9jlzZvUNAz6SYDJXrD5f/kw1SJQtldfmVV3wmZ0AW7PyCU2ffPmsBY7EqF6chgRu3m/KPbHysNyBVQB35kt9wsvytPvuhhAySAGcZ1zcygtLZEWtBwoCfasEt0oJxSaScy9hwtl21MEKQIzh6fs2OdQFs2wDEGQEU6sNZQ==', 'STUDENT'),
('jakov.vasic@student.unizg.hr', 'eb13bd75dbacf073f54f4692029bc69c36a1877ec6db23588dbb82790fd89f6a', 'B9kobUCsE6hsB8Bk++Tbdg==', 'cbu/nFVzR1r0hxKej2AYgWrG1FT9yRoPvUxveZ8qTG0AihkRG9fxAdab+ihMPdGowL7uh2bsyx5w4DUtE86t2SR06We3BmwZtoxiin1TQw2Vmt2Ok/hLnSq+b9N9374qzzqtxiL+tBBzgGgwWoA0TOEVi0F2Wx611H62xD6dTVnKqizshFfRiFBCH4+Lr7YUsHz2qrs/lebybo1Vv2JIA1Dt5eY5rS2sNTGNqdavC67HILJKEJYKCqwS4BnMm/9zOIzBrprRNK64WAdsRk7x65XrvpV408OeQZICo/8XDP+kxMKl2nNi1TjhNfUPSJMqoKfLbMX2Djw3nEEjUprnVQ==', 'STUDENT'),
('tea.saric@student.unizg.hr', 'd6a95de86614b34409eb99dacefc6f8286f46e332fecbad05ed861ed2c48193a', 'lrPfMsqP8xBMpQdYAasC5g==', 'TtnQHf7cgQmE6OXjjrBNwJ9s1nXb5eSNPepNU7voVeFPzLB6MtOwD7Za3E7zh3xrrMFDF4+jKZN8Pt3SZ5PBklbNBTt97IbLYdtRvEn8aWTkCPoGUMtmmQN2Lc/ZSfzMdshI3hA4jHJNFL1+mZGVpqNXL5IZQbDUHNBHcs530OZONgeisMbm7Gn0UnZSiH0JKaqU/wBoE4VJVPf8ejIWpCrcuMeRUeQiGvqjbtgo/Nsl8EBvwSB8JbrLuzi1F8h2kYPHhQEoCnhM4GnCaOv4P4l+JSOp25y6nuL4wY8VpQyOm0l7KCOUZbGGWs5bhfGTT+bHhO94iKcOwIxO1JnTPw==', 'STUDENT'),
('noa.peric@student.unizg.hr', 'a44909b1e1e6b4ab5dd8652accd046635705f316e714efb756a9c708bb10c2be', 'mNkwWVo533SwPdWFQL4A/A==', 'IFPxIuRp2jWIu1VRBXEhpcxAW+fpJBRsHqPx/k+G7QXNq3NSnt5fJ9+JPOusJ3gP2MLiN6VfOHXGrDjXK86pdcctzSXqAsHupxeEUZVu5h8Bm6NCCyMkNJyMufsFIDVYoSe9nTJ4Pj+8oH7ihE32nKc2K0/89OylsbXkLnoS6B6T+vsdoM7Uj2508VGCufv5lfoyEtuLGBhSrPZd3imKEXAPJg3impRoIAHwi/TtDaHvzYffGHxguFLOlG8D6aG7w/2hAmhg9JdH4RloUUKFAs1qO8SY4HkS1gRsHL7H1LYOzToxNrm1KJrodDdCWJENB0RHF4rV1EANEcjft9iwUQ==', 'STUDENT'),
('emma.antonic@student.unizg.hr', 'b83df09daca090f66a0b38af80b7fd7b7903aa631f21850c8d09fdedbb93aecc', '02CWiOE9xWGuGaYf4RCv6Q==', 'YoB2rOI1+ME9hvrKlbaMPj9p1XsMdou7mEEOlGSnremacip/b7puiPx2fQu6EeTgcEOCfQhPkCKLTNLM4gR7p383wxd8WEU2wKCurSmyR3JaP7w5+pk+BeMUgJtIiWOlRez5hSa2WaT0x8PSNaT7HNPKPFVOuvw3/57hSwwB5VL/jbZIUFidL9NvbCdgkgSGn0rx+WE7WnwOiRcBYInPmkLGSIC6qt7EfTDWsJtRcCAiM1NFlAvh1gqh7aLU1F2LQ9FNiVZLeWNFnDugge8hqTeiLXeVKQIL/oBPPwd7etRA0soR+TqZlZADVnkxwBNEvMeeNDfZrYvP9GycygOkeQ==', 'STUDENT'),
('frane.marjanovic@student.unizg.hr', 'a99de06c2d3a980d8ca73d63bbdfadaee8b39ff12c557deead7b55b814b3cf32', 'POthEVYXDmyjzuzmTHUWbA==', 'S8/gqCbsp3nm0D9Yd8tv58i+4F/5mUefgQN71z32r5gNH/P2riihbIyq7vujjy5qpXJQ7R7ZY7qDRi87+aL/UEKo8YJ0AFXRr/NF309BTe5txQmdWJMUrSQ+0VLvzjZkakv2stRTArjA8kx98lam8obHOoFV6qIS8cJ+OUsmZ6mp6SAq5Q0B5z9vZec4VRMVu4v5fzei5SQXwqmGbogxxl+ITAEZW3yvIkQvJEJW6qJQLCoMXW5yoccjDKCdvznau6COsDvbhKnkXE/6TeLI/1+xAmcBwDeQqhemKo/8kpzp9d8RPBuWicc7XS9TjKxuLiSBdGmc8y6wFDsSs3Q3Gg==', 'STUDENT'),
('lana.kos@student.unizg.hr', 'b98568f3c81c9714f678ac40a99fb1e0ca36be9a1c8a41e19ad029cf6a941971', 'b2VbWFfMQZztwT6BQ7aRwA==', 'DSiRV8FtNQUNSB6nu/vT+o8eaboMyXM4iaQ+GIuF20Q9Kphd3V+AYaS9Ug0qgyCZu9erkFGwGpF2OuX6odUC5c+WdpNX3tQogj4z2ZbSnL/HxZ7+3DCrzEZifJKNs3Bj2Hz9XO+19x5P6zlJekIfGOpmXDvrt19u566FbmT/PGQzWMsHMMNvCheOCKZ6HJpeF84fABJsY41ZtUnsF1jz25j6jox70w1B9lR8aaI1bz/UiJgWSDie5dbhPtPDYsxJog0rRdE48ifq23faT4QINtE8wA5OtRF42Qk/iwbxUiJeBQ5U3eDaB9tzWzyiP90HozDyeXhnZUeYV7pMd/hd8A==', 'STUDENT'),
('viktor.miletic@student.unizg.hr', 'b6307af82fd93d8222347ab864a8d63d6b888d5a050a07019b3c5013427174a5', 'GEm1zMywBeBmWpi7FioMbQ==', 'Dlj/jVXNO8WwuCEmELhsVdY8Od+n6nDuT5fNbqJXSUT1NlBuInFb3CjvPlC/Lj6fxb/Q43/VWS52o0XSUpTeyuHW+cb1lg6O8W2RQlDb4pjMj92qlSR14Y32mW/VOUCS4XdVUttE7Mp1RRLMEvKtY+JwYfK1Ta6xjvmB6lPgr4maUF3EDf8xdbklz2uehy8KA8ZnuSddf90Xo9Fh7alAN7thkVBmb19eKZKw3YbSqFgTrWnnIwaXFOaCPJTtu2lK6B5+jisoamAdFM3pt9hNWBZCSTIjgulAL4yTf4Em3kAkLeRZo9dVaKwiCvE/wTY3LZpiNPpo0KcQc99jluP5aQ==', 'STUDENT'),
('lucia.stankovic@student.unizg.hr', 'a3f249e88840dcb517365b8a5f9ee4ac8950f110807d9a36848dc2049444dd65', 'krXfabNdwepmeWwmcaDNDA==', 'g268aijK9ci+7CMMVHa7LisgyXe77TtysIg25VwdfCJC7dWzmXuLkFEHXO0WXXKcDRB5kM/tEz0YjosMpHZCfUTrToxA59AoGwVJg85De93W+brBl+W1DqK+egOf5VLzMkbtLcs/AMtSu2VYl0DMsbtLnVLcutVDZixp0cswdiAuNxhOsMXK23oaGUlroZt7C/1Xvw7Rg/PDc3eZ9kVPK2k3ea/GvzRK54vEaQX7Q06T9ru+DEnjMnKhCQOBSh7L62MnCvZDfUKgk17fgzGgNoVogCGUocQ92rrrHSytOZ7hjk26hoRUL3CP59YoGmgLDoRec1K8InIAvro9QFsNaQ==', 'STUDENT'),
('bruno.matic@student.unizg.hr', 'd597a659ee6bd910c80473903c1c36f0d591051080382b675f599e49e3f708b2', 'KfYRE++Y+yDYiARUQbAENw==', 'e19dg55RFlfqxpuyh2BZVqcJYcKH4gZkEFQb4Zv+qG4No6NGo9GwXyL7HJvJUOIUM6xU82Sfsmq3hcb4xWqvqS0oWtoObTGkcyR9yZ5T9LLsVM81jkPNuICuhhp4rXCSbLCEWxji6Cp1pj/fZjTEFP0+cOzs1UtomLEk3GtDLAUxnRofj40ol/XmKD7CMd+tTL/pWcuD8kwGTJyFmhltCm4D2AjLmSiruce11pKK2q5/U5lVHUr2bvEI275toUB0BjqOO7ZiqYLxq/el8CmYwTWlZJgfrXixxg8D3cfCaE8GrF4SGiEBdGOL6XlMs1TsrlqmCZbwVRoDBdbXPypI7g==', 'STUDENT'),
('dorotea.petrovic@student.unizg.hr', 'a2f456c008477bcea56b9c6ab4f0d28749a32e36b6c8f7e19de450669abeb70f', 'qo2A9VAYwf5nf1I3E2OX3A==', 'AcuZY6bYzZV4Wb50qBgsuNw/Zf13V5q5m1N6Zw8dw3cry+mYGqql67S2J+E9hBxImFfcMqWVwspQdf9yAVfDdV9NhkfU+APY9OV2534FCpbdpvQ+/4xzJsQtJDt5RUnZpmlNd3tAQAFq2t44gUwApwaKzbgyNTLEFocuDxIRbCssEDVHtvG/tQPRUZ4kMaX0B1ganpok3kZHGEiiS17dX94G2XEE607ac+hTx4tpfayGf9WZZneBo/dXmj/ERuPAIcnFUqkPA4N9GJv5XpwAATcDhKLMJ2AUOSXBD60jivWsosAS8qZ7HfQeVTOMqmAkCrFyo5wkNEyTD8yH2Bkulw==', 'STUDENT'),
('alex.loncar@student.unizg.hr', 'f3905b285f525a510555dde3dac751524c852b14352ade623e994ead2c849987', 'Tshfu5MnnrunAMGnK3ifFQ==', 'bZ+PgSl8jpqNN6GRnOeK9yZ+kIHFJUXUrTDZNJaP0Iac/nkt9nOMqyKlqtzCod/2v8R6dzY27xf+he2WibTkQmEi5tkOAkMP7uk/VhyE3TwFSv3mXQExLQWX+h1y7oZHRTxa2Zq2X+bPqUu/2XWxfRXXj717McdZk4qJX40VD0C9VMYoi0VstvdvMCaGOtK3u3SBJ7ZfHopHGIGItYhwvJvFnVMEyXOXqiCLsau9Pkq7Pj3lLlnMWD2Z+MaxW0AaHn/j7jgh7aG9v+5z9hJRCA5adu+9YB7dl9ZdKV5Y0SJQCbYQSsAIVj368wa36cA/8rD9EzwLAeQG4SV6fklUGA==', 'STUDENT'),
('klara.barisic@student.unizg.hr', 'c297823e173916a95c3e4ff0cb221f7b7cec6ce1d242e2ae2e2430f0134d9a01', 'Nv4+GMxePvL69VyeI8MJNQ==', 'gto47/rhNpnxVMzXhpeh8ij4Rvl6X5ClvBWMtrwwp7bvUyADzUOD0ulV4ZWLs4J6rZBzlUlPvJWDYxB9B6WOKASObugbb56E1Q+2tytrym7w4Bbi3gtt/Mir6c0dEkw3uS/p50eF74ElCe2GlMDdSy47Nr00GM1PKQvtke0fUznBfSJJJp8jjIwn02Aq2twONKHcOP5mUftv8HkAaQH0HxD/S1GeD4lRCFvQbHuV4KfpdtQaZX1uvlo4yBtIZz/AG88uKs9C+x8eHSJ5bkgWgJxC6zYG5C6AHuiBNmaKgZWj/y35YU28shh+1PWNc0g8LXnxfuWMTSmWkfrph23+Pw==', 'STUDENT'),
('niko.popovic@student.unizg.hr', 'd39dfc1e2734b948823bbbffb00ee0602d79ba474fc9d0fa3477547eea0dc9da', 'XtoF7eIc2OdHGc4Bb/dZ5g==', 'XSrVQ8HG/nZeigfmp+/e/L2MVFm65UxSodiGULct5dKuaMa6FxgM+G9fQgw+BmbidjVQ78jVKiYRNJz8e7Ppr4CZq5fu7lNgiQt9s1itnqJE2qu8w403+53anCQqBpalvQpCKVuVDASYxMhq7ehgn+LqaIJwkaUUCmCLbWJkbC7k3VgIz9T+ec9B4BHiEoxzBSDen3bTfu2rB5LBW1nHdlHntE/IEoXSufSVXqMFM/agVKQ5S0QfPcOuKHlgBOLLRFe7E2oEG1YXayoFsqNZzgdjt2piQzIVIH6WyOHuD5LGR5nNQBbO1GChSJI/KWtN46YYcyhltTZvgs5mFjSe2Q==', 'STUDENT'),
('mila.simunovic@student.unizg.hr', 'c9b1cf7028365dc4450170ae1e79163d8da7cc12d536fea2513600038856ce20', 'HbFHTPRksB1rybgxXY1pQg==', 'WNmlRdc2kJxWZBg0XqFQIvlX5WW0gmohEkzsN0nFWVZFRogAdTQUDi1rVDx9blFmOpaThJykfCpf1K/NCAb4pleHpCDnHqEPTTlijpznkHt3vRu+O2Q7kj9AYsObA5EALb+nCaAethk/L2T/aFRa+fAwpXwPEufbDxP7b64oOKwYUo+tVa3HGaulzUF0DInJdzm1slwvkWV6AfuULosd6/i2J4DvnJgFFAi5VodmoodOvVVrP974KlBUnjRLSuOyEoc9A4cvTDpqV18ykLYilsRotayScib+WHrRYlCKSX6YUIjHbUNfGUyqLFYU2+ak4gg0T2vAoE9/eI7KvituHQ==', 'STUDENT'),
('miljenko.kovac@prof.unizg.hr', 'fe1b538e33dea3761016c7338150b5435a4ff7b473573010def604b0bd678689', '1z6qBCQqkgW60GUreGYAIQ==', 'LTEFWqbJdN9T933udUeTYMDIEmkE7CzsRBEXOxg3F3fjFg7U7wG436rPIAzp46D1E+3yx9SehsOIJuVHHDkVfaR9drjP4QWKmnYAZBrZzv6ZQh4NgNdQ/Qeg01u7YggJhpHp7DQyGAetDcsV1u0ptNlMfvptoiGBBQc15qt4BokK+gKi7PKNzMKYcY/1cFxvKNBlFwOkqspEFUCqZtHBpqbGSBW9GC6Jhx+ZNPuYf+vkQDu3elVN6VXGUlG40vdOsjZDm9YKsSQ7gzxSpArEFWVyQRVlv+mTu/71KPyn7LEK5HoRyzOVJvEty0j4gXdFug6BKDueI/pngDUsTj/7dA==', 'PROFESOR'),
('dragan.petrovic@prof.unizg.hr', 'ddd76e5fdaf6d0c026be07c2b93c701a4a0629b898a43777fbf067bb2382b5c2', 'CewhbFqYMsqgHihlhNQ2pA==', 'hocFKIzmEq5wuMGUsCTxRpFvK7cpXiRah8ty3lrWwwmODlgOZGu3Lmwbx453yI+ZUjOIF5UvCcw+2x36Mg9urlSldg+pjFdkck6N2MPbJyPvLJQk88QAWs9pj8pqA+Kpe3BUp6o8ev2G3P4XBwd1nO+1BloV0XGrTeptnXSDU56Hcg+ZoUD6IPzddx8wAABGRAr3AoGwUm/27tIMDf/2bWoaSV5ruU+vrd37COVVtMSqbpqIv19rpO5Ej7mpGaMFhRtZydqiTYnzWJ5MZznfq72wBIDu/X7fRDWaW3FK22+YlBdsE5SslagvXMIadW4W0xeD7qWxuLc4jHipjXJeFw==', 'PROFESOR'),
('ivana.matic@prof.unizg.hr', 'faf21bf26d8c6e44cae62f28c5e0912b113eed8203a23a9009b907b88a3840fc', 'ZW67asQDQYDwrghylru3qg==', 'SdLlxMHvDKhmcy5xY58mrO2rZhj8nhyWYzgBtnJvhIEa0RXS0DX9V1oNu2UziV5kRgBCqfQgF0drpdgu/ofBsaRCUAQQhcQn219iRqwZJ88tfKqzih2IL3FJe5onnZyIRmWVs7yYvxic0H7WDq1ReyPbKzIu0yf68g2qQ5+Fx3hLAuCsl3N1hyJuNY8xWdzE6kWf4LV1hXAq6ya4mUfdEpYW6lXO32rOwX3zPCeZgXDRHoCdWRdCXwXVas4RSHws60Fe/2M5mqD4YLq+FX7pr1+Fgfjb4FmR7I+eQVc+02TBFJ7hPKPT5cjDgD2Um2RcTNkAtfPt+VrV/L8Kdk4O4Q==', 'PROFESOR'),
('ante.horvat@prof.unizg.hr', 'bb97d465d9278980aad54abb06432cf4c4098022928bfbd1096eb002263fe3e5', '80shgt75DL5QfcWBWaENqA==', 'l5XmAb3WQ//wSlmBju+TGPrsQMNH6zfx2lPseV19AFxQbMK74DOEeorZX3hvWoxDmCBKrDTD/bL/zwEwjLMnGYwnoiBQN8PSJ1iqi3Pbhe2lkW3Knwc+lpecu7DBcIlPl8EJuDo4WkMHBJMoT4m1gMEgA78T2YGom2rXVBIN8aP/RZOeRGsysW+DcTYpjsXszNUWvYdiPc74Pyoj0dSKQO1TE6GFWGElwCxan8dlPRMMl+8XDD0mSIh2jgMNI2C+8kMPw5Z+jmYwTL8xvyD7BvuHsUeoSSGbfBarsAMGkcFscMRa5drL5HvGtj7NhtwxZi6S/Qlf6LBF6m8EJYb67A==', 'PROFESOR'),
('marina.kolar@prof.unizg.hr', 'eec66cc609cbcc902b3a834cfeda12eb061a4d3c61aa2355a62f905f69485708', 'Px2o3+mWfHknxTfwXwa5cw==', 'QM0iteJvyWUls0p/UCNbAcvw1z7JZhYvXig6dFlHcrlgA9qOcgxjKJVSQjJYOo4Nqi2TtqOyYGd92obO/kR2Sf0zxDYM4Sls+5sFQL6bTIQvPN/kMxWBnOnWeys5dW4H5j4dE2sIO2Mos38eRbKUGVFr1eZjbZTu8o/vj5BI2kpH25zpgo2Vlxz50jx1s7L1qN4/lLbivaP0LePWMOfYziJdx+hjjsN3NZfZ+3Li3RrjY8wl+3YqWZkNHlZaNhXy9yIfIhxXH1MFXYQcwWUO9Dh5BxyOZFqKbu55KeoNiATiAnKRxyA9JLcH6f41/1NaZnQTBhkeVwcNIYqgc47JzA==', 'PROFESOR'),
('zlatan.ivkovic@prof.unizg.hr', 'e3d9234d4956bbae15da19b3ba5f499a6f0e1d7990ff0179f2719951fa8322d2', 'VB0woNcNRbt1w4J7kL0ewA==', 'G1MREEcZV9YDWb5KiPpAEBNKrIGEFZ32EWM516hQTbEx3KDo7i+guMNe+GSbN2n/9V2hHdgjsUyNfbLyaamsvyLdEZpNlHlTB7S/UvmsJg/6HxCe91WuTplVelpBmjGIc+n4LCF7TZChX+ryShkljr/O6870tlNUz5JQTQ3J6/aHqDgMENov0jVUMQBvnzz0bsS9TTtFNQBxIbQViIm3XTgI+lcZxmm3mefAQLld0ZjTMB/mSjdMJ2DFHYMHojsW3w384vfp/uhGiXkKH/pzPhs4qLFsPFBX8YHGhoiRK8pFVwjO5LEmHm1UutbmX/9q7Y/VEhNm+PYbvyNpWM7k1w==', 'PROFESOR'),
('sanja.milutinovic@prof.unizg.hr', 'f6ec48bca130986a254966fc4d09c92c62f4625c38a131cfd1b8a1a16f8b5d64', 'R8hq0qPOSNzhWjkRzaSfug==', 'DchirL0uGEn8Btl58IjhNXadA8H7Yd5ReTIEuer+ohBWK4YkwhGzl+aHm/s7PKABhegZvo1CcadExhFjxTYSTrB9RR1Rvbs82FrnGjA+RMqtRQBmUbKoH+KmTFioBVVm/uXRzXWn92GHhtYfNYT/6HFBX5HafuFgrgqqS83RoOlY9y0Ki09ahWIeLHdsVekOTS9Lpdf1EhGmRYXqfDN6OJtycfx/DCrgCBt1+V2uSs0xsCe+8tFsufgVxqDM9l5OSGrchlRU08rAqR5Aw0BkrTZOr50XQVd/99C0pmIvueLrgac7nqbC2Nh+MJzi4NG10sQ6+y9YCEDJ8HqcLdFM3A==', 'PROFESOR'),
('boris.novak@prof.unizg.hr', 'dd0f3e32028f02ff513b66cfb50627c300805e144d32950ecf373ce29c1ad689', 'WJkzSMXAhl2Cupco5dIwJA==', 'RQlYMOUoPdsgpzJ8GvXHe3QJo/Pvr9nPPJO6wDg5ujafm4jdV3JEG1HNzf0FY/qZh2FXKw8qyqja68RUOeoomORu8a++1bg2cnzA2fniV7R7tghRPVNk6UwdsuxgutVN1EcWrgtxysPLBFlP40MCpItGm/TN7jBJH7rPa6G3Lcny0PVYpR+7Et3V+l1fa/uh41nto5R3Sz/4F3bJlfPYkGRkV19SW2BEMexbDpOsygZaAo9gxuU6I1pImYG5yzalcbfAocxQ1xJtTAZiO+XGhn7LLNb+K1C9DnZ5hXzo3PzI158Q5dWnjB5IaqeyrSpmQJuyhdt593IDR9fuKm75ZQ==', 'PROFESOR'),
('dunja.simic@prof.unizg.hr', 'f33cb57458d7c27929d2d7c568fe6fb1d5730d8d09b89ae7ca4b2b77b5bc54a8', 'NjnqLzyUv4v7p1Tjr9sh2g==', 'iITd0e3GxMEU2ncDZ9gXa3oUNvT7hFwsqijuvIZ1fdKqfu3cZ4Dn1VG5caXrbO7Tjql92Uxm3vZJNmEtAp3tgTGiDMozrNxLruCLKFzb6jNFLXygatMOwwhIH7HwgX3dq/47/pypDYKrpu0AswQDdlL9oPK/yEiIn6lF3NHh8zeaBMnZsFxJn+l9xjZLk6KvXT+/u6M/xkhmOr47ksQh8D5WdhnZy9vtAJVC5gB1GWVxXcTd/xN07pYOniPaLep2k7WU+zPWbWcHMosfqJXHfrvolHg2SMBNn2IGmVNM6geNdVMzh9hNV23mXIiiunONd1o8zl9FpqQY+f6vza/3mw==', 'PROFESOR'),
('igor.mandic@prof.unizg.hr', 'fea859c7f95e2664af492c38982f8b6cc3e09d7528c634da248fcc4a53571d09', 'FX8EbkiUgoCsgEVrRsu2AA==', 'Ksf29WPjtKvsDHxPKfpbHvEy6kqy+R06nAZ+Q5f0L3ySBWAc3QOi3m6kvybfRuluCEPKwl+E9r3YObvwFqSbRVhaQKZTBtK3c1c368tfG4Vw7N2hXMUvDMcCCZnC5aAljTDVgnJtI85aGkEvIpSP2keooCOv57LDmtJ+GSpiL1u7aO7iOXhNQmTqhIdl9zz5tqUH2+F9tDqw5UFUkbzzGdFL1+OG9nZUuYYC5FZWBi/usZw+DsmBql/KyzGFOmM5umTNV2uL1/wAIILCT6p930FmEGzFd1bPDcr08dwYmNzKxcOtnu1nErKI0puoSxao39Vkys8fd/xQXTpIiPRQ6w==', 'PROFESOR'),
('admin.sustav@unizg.hr', 'bccf8fffd1d82e5a38c80677b2dcf91b3150310f73ed6e59a9a6366d5bb70894', 'xTQKVth2jTxEsVIayley1Q==', 'PcDU51J+8roh5oD6cdVK/fam90Y0OatGBnDGs7X+qx3Bi3sBhHsnBvGXHemwIVD7wZp2TUMHXvfVzQRf6oYWMQZExygf6jyuc8V3FCwhcxt0LL4o86zuKeAYJ/f2X0vhUBX0elNCNlWp9mqv4F0eTreal14NjZ9FiWlVPUVfW2zqdhU/anN9q5JmReDLxP8E1qPxH38+DAmkiPLm96AS7tAK2Jzk9rITzbAA53kZLgnPZde3bhXqfyA7v+OInnezlyrjpBnKMhQjb7/R+AN3Au8zhUU7Dr6oivjQV2ClGTgvivyuXZtMhY5j6OexzoxWb7Qw3YME1lxKViKjUyiODg==', 'ADMIN');

INSERT INTO student (korisnik_id, jmbag) VALUES
(1, '0036001001'),
(2, '0036001002'),
(3, '0036001003'),
(4, '0036001004'),
(5, '0036001005'),
(6, '0036001006'),
(7, '0036001007'),
(8, '0036001008'),
(9, '0036001009'),
(10, '0036001010'),
(11, '0036001011'),
(12, '0036001012'),
(13, '0036001013'),
(14, '0036001014'),
(15, '0036001015'),
(16, '0036001016'),
(17, '0036001017'),
(18, '0036001018'),
(19, '0036001019'),
(20, '0036001020'),
(21, '0036001021'),
(22, '0036001022'),
(23, '0036001023'),
(24, '0036001024'),
(25, '0036001025'),
(26, '0036001026'),
(27, '0036001027'),
(28, '0036001028'),
(29, '0036001029'),
(30, '0036001030');


INSERT INTO profesor (korisnik_id, titula) VALUES
(31, 'Redoviti profesor'),
(32, 'Docent'),
(33, 'Izvorni profesor'),
(34, 'Profesor dr. sc.'),
(35, 'Docent dr. sc.'),
(36, 'Profesor'),
(37, 'Redoviti profesor dr. sc.'),
(38, 'Profesor'),
(39, 'Docent'),
(40, 'Profesor emeritus');


INSERT INTO admin (korisnik_id, ovlasti) VALUES
(41, 'FULL_ACCESS: Korisnici, Studenti, Profesori, Predmeti, Ocjene');

INSERT INTO predmet (naziv, sifra, ects_bodovi, profesor_id, semestar, godina_izvodenja) VALUES
('Matematika 1',        'MAT101', 6,  NULL, 'ZIMSKI', 1),
('Programiranje 1',     'PROG1',  7,  NULL, 'ZIMSKI', 1),
('Osnove baza podataka','BP101',  6,  NULL, 'LJETNI', 2),
('Algoritmi i strukture podataka', 'ASP201', 8, NULL, 'LJETNI', 2),
('Operacijski sustavi', 'OS301',  6,  NULL, 'ZIMSKI', 3),
('Računalne mreže',     'RM302',  5,  NULL, 'LJETNI', 3),
('Softversko inženjerstvo', 'SI401', 6, NULL, 'ZIMSKI', 4),
('Umjetna inteligencija','UI402', 6, NULL, 'LJETNI', 4),
('Sigurnost informacijskih sustava', 'SIS501', 5, NULL, 'ZIMSKI', 5),
('Distribuirani sustavi','DS502',  6, NULL, 'LJETNI', 5);