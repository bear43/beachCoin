function makeNameString(str){
    if(str.trim() === '') return '';
    str.replace(/^\s+/,"");
    str.replace(/((\s*\S+)*)\s*/, "$1");
    var res = str.substr(0,1).toUpperCase()+str.substr(1).toLowerCase();
    if(res.indexOf('-')!==-1)
    {
        var arr=res.split('-');
        res=arr[0];
        for(var i=1;i<arr.length;i++)
        {
            res+='-'+arr[i].substr(0,1).toUpperCase()+arr[i].substr(1);
        }
    }
    if(res.indexOf(' ')!==-1)
    {
        arr=res.split(' ');
        res=arr[0];
        for(i=1;i<arr.length;i++)
        {
            res+=' '+arr[i].substr(0,1).toUpperCase()+arr[i].substr(1);
        }
    }
    return res;
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}