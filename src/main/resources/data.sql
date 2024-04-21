if object_id('GENERUJ_JPK_WB_XML', 'P') is not null
    drop procedure GENERUJ_JPK_WB_XML;

create procedure GENERUJ_JPK_WB_XML @naglowek_id int, @xml nvarchar(max) output as
begin
    declare @rachunek_numer varchar(36) = (select rachunek_numer from naglowki_wb where id = @naglowek_id)
    declare @podmiot_nip varchar(10) = (select podmiot_nip from rachunki where numer = @rachunek_numer)

    declare @tmp xml = (
        select
            ( select 'JPK_WB (1)' as 'KodFormularza/@kodSystemowy',
                     '1-0'        as 'KodFormularza/@wersjaSchemy',
                     'JPK_WB'     as 'KodFormularza',
                     '1'          as 'WariantFormularza',
                     '1'          as 'CelZlozenia',
                     getdate()    as 'DataWytworzeniaJPK',
                     data_od      as 'DataOd',
                     data_do      as 'DataDo',
                     kod_urzedu   as 'KodUrzedu',
                     (select waluta_kod from rachunki where numer = @rachunek_numer) AS 'DomyslnyKodWaluty'
              from naglowki_wb
              where id = @naglowek_id
              for xml path(''), root('Naglowek'), type
            ),
            ( select
                  ( select
                        nip as 'NIP',
                        regon as 'REGON',
                        pelna_nazwa as 'PelnaNazwa'
                    from podmioty_wb where nip = @podmiot_nip
                    for xml path('IdentyfikatorPodmiotu'), type
                  ),
                  ( select
                        'PL' as 'KodKraju',
                        wojewodztwo as 'Wojewodztwo',
                        powiat as 'Powiat',
                        gmina as 'Gmina',
                        ulica as 'Ulica',
                        nr_domu as 'NrDomu',
                        nr_lokalu as 'NrLokalu',
                        miejscowosc as 'Miejscowosc',
                        kod_pocztowy as 'KodPocztowy',
                        poczta as 'Poczta'
                    from podmioty_wb where nip = @podmiot_nip
                    for xml path('AdresPodmiotu'), type
                  )
              from naglowki_wb
              where id = @naglowek_id
              for xml path('Podmiot1'), type
            ),
            (select trim(@rachunek_numer)
             for xml path('NumerRachunku'), type),
            ( select
                  saldo_poczatkowe as 'SaldoPoczatkowe',
                  saldo_koncowe as 'SaldoKoncowe'
              from naglowki_wb where id = @naglowek_id
              for xml path('Salda'), type
            ),
            ( select
                  'G' as '@typ',
                  numer as 'NumerWiersza',
                  data_operacji as 'DataOperacji',
                  nazwa_podmiotu as 'NazwaPodmiotu',
                  opis_operacji as 'OpisOperacji',
                  kwota_operacji as 'KwotaOperacji',
                  saldo_operacji as 'SaldoOperacji'
              from wiersze_wb where naglowek_id = @naglowek_id
              order by numer asc
              for xml path('WyciagWiersz'), type
            ),
            ( select
                  count(*) as 'LiczbaWierszy',
                  sum(case when kwota_operacji < 0 then kwota_operacji else 0 end) as 'SumaObciazen',
                  sum(case when kwota_operacji > 0 then kwota_operacji else 0 end) as 'SumaUznan'
              from wiersze_wb where naglowek_id = @naglowek_id
              for xml path('WyciagCtrl'), type
            )
        for xml path(''), root('JPK'), type
    )

    set @tmp.modify('delete  //*[not(node()) and not(text()) and not(@*)]')
    set @tmp = replace(cast(@tmp as varchar(max)), '<JPK', '<JPK xmlns="http://jpk.mf.gov.pl/wzor/2016/03/09/03092/"')
    set @xml = cast(@tmp as nvarchar(max))
end;


insert into waluty (kod, kraj, nazwa)
values ('AED', 'United Arab Emirates', 'Dirhams'),
       ('AFN', 'Afghanistan', 'Afghanis'),
       ('ALL', 'Albania', 'Leke'),
       ('AMD', 'Armenia', 'Drams'),
       ('ANG', 'Netherlands Antilles', 'Guilders'),
       ('AOA', 'Angola', 'Kwanza'),
       ('ARS', 'Argentina', 'Pesos'),
       ('AUD', 'Australia', 'Dollars'),
       ('AWG', 'Aruba', 'Guilders'),
       ('AZN', 'Azerbaijan', 'Manats'),
       ('BAM', 'Bosnia and Herzegovina', 'Convertible Marka'),
       ('BBD', 'Barbados', 'Dollars'),
       ('BDT', 'Bangladesh', 'Taka'),
       ('BGN', 'Bulgaria', 'Leva'),
       ('BHD', 'Bahrain', 'Dinars'),
       ('BIF', 'Burundi', 'Francs'),
       ('BMD', 'Bermuda', 'Dollars'),
       ('BND', 'Brunei Darussalam', 'Dollars'),
       ('BOB', 'Bolivia', 'Bolivianos'),
       ('BOV', 'Bolivia', 'Mvdol'),
       ('BRL', 'Brazil', 'Brazil Real'),
       ('BSD', 'Bahamas', 'Dollars'),
       ('BTN', 'Bhutan', 'Ngultrum'),
       ('BWP', 'Botswana', 'Pulas'),
       ('BYR', 'Belarus', 'Rubles'),
       ('BZD', 'Belize', 'Dollars'),
       ('CAD', 'Canada', 'Dollars'),
       ('CDF', 'Congo/Kinshasa', 'Congolese Francs'),
       ('CHF', 'Switzerland', 'Francs'),
       ('CLF', 'Chile', 'Unidades de fomento'),
       ('CLP', 'Chile', 'Pesos'),
       ('CNY', 'China', 'Yuan Renminbi'),
       ('COP', 'Colombia', 'Pesos'),
       ('COU', 'Colombia', 'Unidad de Valor Real'),
       ('CRC', 'Costa Rica', 'Colones'),
       ('CUC', 'Cuba', 'Convertible Pesos'),
       ('CUP', 'Cuba', 'Pesos'),
       ('CVE', 'Cape Verde', 'Escudos'),
       ('CZK', 'Czech Republic', 'Koruny'),
       ('DJF', 'Djibouti', 'Francs'),
       ('DKK', 'Denmark', 'Kroner'),
       ('DOP', 'Dominican Republic', 'Pesos'),
       ('DZD', 'Algeria', 'Algeria Dinars'),
       ('EEK', 'Estonia', 'Krooni'),
       ('EGP', 'Egypt', 'Pounds'),
       ('ERN', 'Eritrea', 'Nakfa'),
       ('ETB', 'Ethiopia', 'Birr'),
       ('EUR', 'Euro Member Countries', 'Euro'),
       ('FJD', 'Fiji', 'Dollars'),
       ('FKP', 'Falkland Islands (Malvinas)', 'Pounds'),
       ('GBP', 'United Kingdom', 'Pounds'),
       ('GEL', 'Georgia', 'Lari'),
       ('GHS', 'Ghana', 'Cedis'),
       ('GIP', 'Gibraltar', 'Pounds'),
       ('GMD', 'Gambia', 'Dalasi'),
       ('GNF', 'Guinea', 'Francs'),
       ('GTQ', 'Guatemala', 'Quetzales'),
       ('GWP', '', 'Guinea-Bissau Peso'),
       ('GYD', 'Guyana', 'Dollars'),
       ('HKD', 'Hong Kong', 'Dollars'),
       ('HNL', 'Honduras', 'Lempiras'),
       ('HRK', 'Croatia', 'Kuna'),
       ('HTG', 'Haiti', 'Gourdes'),
       ('HUF', 'Hungary', 'Forint'),
       ('IDR', 'Indonesia', 'Rupiahs'),
       ('ILS', 'Israel', 'New Shekels'),
       ('INR', 'India', 'Rupees'),
       ('IQD', 'Iraq', 'Dinars'),
       ('IRR', 'Iran', 'Rials'),
       ('ISK', 'Iceland', 'Kronur'),
       ('JMD', 'Jamaica', 'Dollars'),
       ('JOD', 'Jordan', 'Dinars'),
       ('JPY', 'Japan', 'Yen'),
       ('KES', 'Kenya', 'Shillings'),
       ('KGS', 'Kyrgyzstan', 'Soms'),
       ('KHR', 'Cambodia', 'Riels'),
       ('KMF', 'Comoros', 'Francs'),
       ('KPW', 'Korea (North)', 'Won'),
       ('KRW', 'Korea (South)', 'Won'),
       ('KWD', 'Kuwait', 'Dinars'),
       ('KYD', 'Cayman Islands', 'Dollars'),
       ('KZT', 'Kazakstan', 'Tenge'),
       ('LAK', 'Laos', 'Kips'),
       ('LBP', 'Lebanon', 'Pounds'),
       ('LKR', 'Sri Lanka', 'Rupees'),
       ('LRD', 'Liberia', 'Dollars'),
       ('LSL', 'Lesotho', 'Maloti'),
       ('LTL', 'Lithuania', 'Litai'),
       ('LVL', 'Latvia', 'Lati'),
       ('LYD', 'Libya', 'Dinars'),
       ('MAD', 'Morocco', 'Dirhams'),
       ('MDL', 'Moldova', 'Lei'),
       ('MGA', 'Madagascar', 'Malagasy Ariary'),
       ('MKD', 'Macedonia', 'Denars'),
       ('MMK', 'Myanmar (Burma)', 'Kyats'),
       ('MNT', 'Mongolia', 'Tugriks'),
       ('MOP', 'Macau', 'Patacas'),
       ('MRO', 'Mauritania', 'Ouguiyas'),
       ('MTL', 'Malta', 'Liri'),
       ('MUR', 'Mauritius', 'Rupees'),
       ('MVR', 'Maldives (Maldive Islands)', 'Rufiyaa'),
       ('MWK', 'Malawi', 'Kwachas'),
       ('MXN', 'Mexico', 'Pesos'),
       ('MXV', 'Mexico', 'Mexican Unidad de Inversion'),
       ('MYR', 'Malaysia', 'Ringgits'),
       ('MZN', 'Mozambique', 'Meticais'),
       ('NAD', 'Namibia', 'Dollars'),
       ('NGN', 'Nigeria', 'Nairas'),
       ('NIO', 'Nicaragua', 'Gold Cordobas'),
       ('NOK', 'Norway', 'Krone'),
       ('NPR', 'Nepal', 'Nepal Rupees'),
       ('NZD', 'New Zealand', 'Dollars'),
       ('OMR', 'Oman', 'Rials'),
       ('PAB', 'Panama', 'Balboa'),
       ('PEN', 'Peru', 'Nuevos Soles'),
       ('PGK', 'Papua New Guinea', 'Kina'),
       ('PHP', 'Philippines', 'Pesos'),
       ('PKR', 'Pakistan', 'Rupees'),
       ('PLN', 'Poland', 'Zlotych'),
       ('PYG', 'Paraguay', 'Guarani'),
       ('QAR', 'Qatar', 'Rials'),
       ('RON', 'Romania', 'New Lei'),
       ('RSD', '', 'Serbian Dinar'),
       ('RUB', 'Russia', 'Rubles'),
       ('RWF', 'Rwanda', 'Rwanda Francs'),
       ('SAR', 'Saudi Arabia', 'Riyals'),
       ('SBD', 'Solomon Islands', 'Dollars'),
       ('SCR', 'Seychelles', 'Rupees'),
       ('SDG', 'Sudan', 'Dinars'),
       ('SEK', 'Sweden', 'Kronor'),
       ('SGD', 'Singapore', 'Dollars'),
       ('SHP', 'Saint Helena', 'Pounds'),
       ('SLL', 'Sierra Leone', 'Leones'),
       ('SOS', 'Somalia', 'Shillings'),
       ('SRD', 'Suriname', 'Dollar'),
       ('STD', N'São Tome and Principe', 'Dobras'),
       ('SVC', 'El Salvador', 'Colones'),
       ('SYP', 'Syria', 'Pounds'),
       ('SZL', 'Swaziland', 'Emalangeni'),
       ('THB', 'Thailand', 'Baht'),
       ('TJS', 'Tajikistan', 'Somoni'),
       ('TMT', 'Turkmenistan', 'Manats'),
       ('TND', 'Tunisia', 'Dinars'),
       ('TOP', 'Tonga', 'Pa''anga'),
       ('TRY', 'Turkey', 'Liras'),
       ('TTD', 'Trinidad and Tobago', 'Dollars'),
       ('TWD', 'Taiwan', 'New Dollars'),
       ('TZS', 'Tanzania', 'Shillings'),
       ('UAH', 'Ukraine', 'Hryvnia'),
       ('UGX', 'Uganda', 'Shillings'),
       ('USD', 'United States of America', 'Dollars'),
       ('UYU', 'Uruguay', 'Pesos'),
       ('UZS', 'Uzbekistan', 'Sums'),
       ('VEF', 'Venezuela', 'Bolivares'),
       ('VND', 'Viet Nam', 'Dong'),
       ('VUV', 'Vanuatu', 'Vatu'),
       ('WST', 'Samoa', 'Tala'),
       ('XAF', N'Communauté Financière Africaine BEAC', 'Francs'),
       ('XCD', '', 'East Caribbean Dollars'),
       ('XOF', N'Communauté Financière Africaine BCEAO', 'Francs'),
       ('XPD', '', 'Palladium Ounces'),
       ('XPF', '', N'Comptoirs Français du Pacifique Francs'),
       ('YER', 'Yemen', 'Rials'),
       ('ZAR', 'South Africa', 'Rand'),
       ('ZMK', 'Zambia', 'Kwacha'),
       ('ZWL', 'Zimbabwe', 'Zimbabwe Dollars ');
