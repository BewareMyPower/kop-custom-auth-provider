const fs = require('fs')
const jwkToPem = require('jwk-to-pem')
const process = require('process')

if (process.argv.length < 3) {
    console.error('Usage: node ' + process.argv[1] + ' <path-to-jwk-file>')
    process.exit(1)
}

const jwk = JSON.parse(fs.readFileSync(process.argv[2]))
const pem = jwkToPem(jwk)
console.log(pem)
