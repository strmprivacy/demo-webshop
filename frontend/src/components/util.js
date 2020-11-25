export default function Formatcurrency(num){
    return "$" + Number(num.toFixed(1)).toLocaleString() + " "
}
