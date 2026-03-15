#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl nao encontrado no PATH."
  exit 1
fi

municipios=(
  "Florianopolis"
  "Joinville"
  "Blumenau"
  "Sao Jose"
  "Chapeco"
  "Criciuma"
  "Itajai"
  "Jaragua do Sul"
  "Lages"
  "Palhoca"
  "Balneario Camboriu"
  "Brusque"
  "Tubarao"
  "Sao Bento do Sul"
  "Camboriu"
  "Navegantes"
  "Biguacu"
  "Rio do Sul"
  "Ararangua"
  "Gaspar"
  "Indaial"
  "Icara"
  "Concordia"
  "Mafra"
  "Canoinhas"
  "Laguna"
  "Imbituba"
  "Porto Belo"
  "Garopaba"
  "Xanxere"
  "Maravilha"
  "Seara"
  "Curitibanos"
  "Fraiburgo"
  "Campos Novos"
  "Sao Miguel do Oeste"
  "Videira"
  "Pomerode"
  "Timbo"
  "Ituporanga"
  "Santo Amaro da Imperatriz"
  "Nova Trento"
  "Penha"
  "Bombinhas"
  "Balneario Picarras"
  "Araquari"
  "Guaramirim"
  "Rio Negrinho"
  "Itapema"
  "Sao Francisco do Sul"
)

segmentos=( "TECNOLOGIA" "COMERCIO" "INDUSTRIA" "SERVICOS" "AGRONEGOCIO" )

echo "Populando 50 empreendimentos em ${BASE_URL} ..."

for i in $(seq 1 50); do
  idx=$(( (i - 1) % ${#municipios[@]} ))
  municipio="${municipios[$idx]}"
  segmento="${segmentos[$(( (i - 1) % ${#segmentos[@]} ))]}"

  payload=$(cat <<JSON
{
  "nome": "Empreendimento Ficticio ${i}",
  "nomeResponsavel": "Responsavel ${i}",
  "municipio": "${municipio}",
  "segmento": "${segmento}",
  "contato": "contato${i}@exemplo.com",
  "status": true
}
JSON
)

  http_code="$(curl -sS -o /tmp/pop_emp_resp.json -w "%{http_code}" \
    -X POST "${BASE_URL}/empreendimentos" \
    -H "Content-Type: application/json" \
    -d "${payload}" || true)"

  if [[ "${http_code}" != "201" ]]; then
    echo "Falhou no item ${i} (HTTP ${http_code}). Resposta:"
    cat /tmp/pop_emp_resp.json || true
    echo
    exit 1
  fi

  echo "OK ${i}/50"
done

echo "Concluido."
