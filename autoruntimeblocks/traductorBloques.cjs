let esjs = require('@es-js/core');
let fs = require('fs').promises;
async function execute(){
    let esjsData = await fs.readFile('./traductorBloques.esjs', 'utf-8');
    let codeJs = esjs.compile(esjsData);
    eval(codeJs);
}
try{
    execute();
}catch(err){
    console.error("Error!" + err);
}