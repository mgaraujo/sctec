Param(
  [string]$BaseUrl = $env:BASE_URL
)

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
  $BaseUrl = "http://localhost:8080"
}

$municipios = @(
  "Florianopolis",
  "Joinville",
  "Blumenau",
  "Sao Jose",
  "Chapeco",
  "Criciuma",
  "Itajai",
  "Jaragua do Sul",
  "Lages",
  "Palhoca",
  "Balneario Camboriu",
  "Brusque",
  "Tubarao",
  "Sao Bento do Sul",
  "Camboriu",
  "Navegantes",
  "Biguacu",
  "Rio do Sul",
  "Ararangua",
  "Gaspar",
  "Indaial",
  "Icara",
  "Concordia",
  "Mafra",
  "Canoinhas",
  "Laguna",
  "Imbituba",
  "Porto Belo",
  "Garopaba",
  "Xanxere",
  "Maravilha",
  "Seara",
  "Curitibanos",
  "Fraiburgo",
  "Campos Novos",
  "Sao Miguel do Oeste",
  "Videira",
  "Pomerode",
  "Timbo",
  "Ituporanga",
  "Santo Amaro da Imperatriz",
  "Nova Trento",
  "Penha",
  "Bombinhas",
  "Balneario Picarras",
  "Araquari",
  "Guaramirim",
  "Rio Negrinho",
  "Itapema",
  "Sao Francisco do Sul"
)

$segmentos = @("TECNOLOGIA", "COMERCIO", "INDUSTRIA", "SERVICOS", "AGRONEGOCIO")

Write-Host "Populando 50 empreendimentos em $BaseUrl ..."

for ($i = 1; $i -le 50; $i++) {
  $municipio = $municipios[($i - 1) % $municipios.Count]
  $segmento = $segmentos[($i - 1) % $segmentos.Count]

  $body = @{
    nome = "Empreendimento Ficticio $i"
    nomeResponsavel = "Responsavel $i"
    municipio = $municipio
    segmento = $segmento
    contato = "contato$i@exemplo.com"
    status = $true
  } | ConvertTo-Json

  try {
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/empreendimentos" -ContentType "application/json" -Body $body | Out-Null
    Write-Host "OK $i/50"
  } catch {
    Write-Host "Falhou no item $i."
    if ($_.Exception.Response -and $_.Exception.Response.GetResponseStream()) {
      $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
      $resp = $reader.ReadToEnd()
      Write-Host $resp
    } else {
      Write-Host $_.Exception.Message
    }
    exit 1
  }
}

Write-Host "Concluido."
