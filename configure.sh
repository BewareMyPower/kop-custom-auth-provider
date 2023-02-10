#!/bin/bash
cd `dirname $0`
set -ex

PUBLIC_KEY=public-key
JWK_FILE=$PUBLIC_KEY.json
docker compose -f hydra/docker-compose.yml exec hydra \
  hydra keys get hydra.jwt.access-token --endpoint http://localhost:4445 \
  | jq '.Payload.keys' | jq '.[] | select(.kid=="public:hydra.jwt.access-token")' \
  > $JWK_FILE
npm install
node jwk-to-pem.js $JWK_FILE > $PUBLIC_KEY

# Create a client whose id is "admin" and secret is "admin-secret"
docker compose -f ./hydra/docker-compose.yml exec hydra \
  hydra clients create --id admin \
    --secret admin-secret \
    --grant-types client_credentials \
    --response-types token,code \
    --token-endpoint-auth-method client_secret_post \
    --endpoint http://localhost:4445

BASE64_TOKEN=$(grep -v "PUBLIC KEY" $PUBLIC_KEY | tr '\n' ':' | sed 's/://g')
echo "tokenPublicKey=data:;base64,$BASE64_TOKEN" >> standalone.conf
sed -i -r "s#\{\{pwd\}\}#$PWD#" standalone.conf
