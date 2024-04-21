if object_id('GENERUJ_JPK_WB_XML', 'P') is not null
drop procedure GENERUJ_JPK_WB_XML
;;;

create procedure GENERUJ_JPK_WB_XML @naglowek_id int, @xml nvarchar(max) output as
begin
    declare @rachunek_numer varchar(36) = (select rachunek_numer from naglowki_wb where id = @naglowek_id)
    declare @podmiot_nip varchar(10) = (select podmiot_nip from rachunki where numer = @rachunek_numer)

    declare @tmp xml
    ;with xmlnamespaces (
         N'http://crd.gov.pl/xml/schematy/dziedzinowe/mf/2016/01/25/eD/DefinicjeTypy/' as [etd],
         N'http://jpk.mf.gov.pl/wzor/2016/03/09/03092/' as [tns]
         )

    select @tmp = (
        select
            ( select 'JPK_WB (1)' as [tns:KodFormularza/@kodSystemowy],
                      '1-0'        as [tns:KodFormularza/@wersjaSchemy],
                      'JPK_WB'     as [tns:KodFormularza],
                      '1'          as [tns:WariantFormularza],
                      '1'          as [tns:CelZlozenia],
                      getdate()    as [tns:DataWytworzeniaJPK],
                      data_od      as [tns:DataOd],
                      data_do      as [tns:DataDo],
                      (select waluta_kod from rachunki where numer = @rachunek_numer) AS [tns:DomyslnyKodWaluty],
                      kod_urzedu   as [tns:KodUrzedu]
               from naglowki_wb
               where id = @naglowek_id
               for xml path(''), root('tns:Naglowek'), type
            ),
            ( select
               ( select
                     nip as [etd:NIP],
                     pelna_nazwa as [etd:PelnaNazwa],
                     regon as [etd:REGON]
                 from podmioty_wb where nip = @podmiot_nip
                 for xml path('tns:IdentyfikatorPodmiotu'), type
               ),
               ( select
                     'PL' as [etd:KodKraju],
                     wojewodztwo as [etd:Wojewodztwo],
                     powiat as [etd:Powiat],
                     gmina as [etd:Gmina],
                     ulica as [etd:Ulica],
                     nr_domu as [etd:NrDomu],
                     nr_lokalu as [etd:NrLokalu],
                     miejscowosc as [etd:Miejscowosc],
                     kod_pocztowy as [etd:KodPocztowy],
                     poczta as [etd:Poczta]
                 from podmioty_wb where nip = @podmiot_nip
                 for xml path('tns:AdresPodmiotu'), type
               )
               for xml path('tns:Podmiot1'), type
            ),
            (select trim(@rachunek_numer) for xml path('tns:NumerRachunku'), type),
            ( select
                   saldo_poczatkowe as [tns:SaldoPoczatkowe],
                   saldo_koncowe as [tns:SaldoKoncowe]
               from naglowki_wb where id = @naglowek_id
               for xml path('tns:Salda'), type
            ),
            ( select
                   'G' as '@typ',
                   numer as [tns:NumerWiersza],
                   data_operacji as [tns:DataOperacji],
                   nazwa_podmiotu as [tns:NazwaPodmiotu],
                   opis_operacji as [tns:OpisOperacji],
                   kwota_operacji as [tns:KwotaOperacji],
                   saldo_operacji as [tns:SaldoOperacji]
               from wiersze_wb where naglowek_id = @naglowek_id
               order by numer asc
               for xml path('tns:WyciagWiersz'), type
            ),
            ( select
                   count(*) as [tns:LiczbaWierszy],
                   sum(case when kwota_operacji < 0 then kwota_operacji else 0 end) as [tns:SumaObciazen],
                   sum(case when kwota_operacji > 0 then kwota_operacji else 0 end) as [tns:SumaUznan]
               from wiersze_wb where naglowek_id = @naglowek_id
               for xml path('tns:WyciagCtrl'), type
            )
        for xml path(''), root('tns:JPK'), type
    )
    set @tmp.modify('delete  //*[not(node()) and not(text()) and not(@*)]')

    -- remove namespaces definitions from nested elements as mssql server is retarded and there is no different way to solve it
    set @xml = cast(@tmp as varchar(max))
    set @xml = replace(@xml, 'xmlns:etd="http://crd.gov.pl/xml/schematy/dziedzinowe/mf/2016/01/25/eD/DefinicjeTypy/"', '')
    set @xml = replace(@xml, 'xmlns:tns="http://jpk.mf.gov.pl/wzor/2016/03/09/03092/"', '')
    -- add namespaces definitions again in the root node
    set @xml = replace(@xml, '<tns:JPK', '<tns:JPK xmlns:etd="http://crd.gov.pl/xml/schematy/dziedzinowe/mf/2016/01/25/eD/DefinicjeTypy/"')
    set @xml = replace(@xml, '<tns:JPK', '<tns:JPK xmlns:tns="http://jpk.mf.gov.pl/wzor/2016/03/09/03092/"')
    set @xml = replace(@xml, '<tns:JPK', '<tns:JPK xmlns="http://jpk.mf.gov.pl/wzor/2016/03/09/03092/"')
end
;;;