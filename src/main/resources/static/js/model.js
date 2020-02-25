Ext.define("Transaction", {
    extend: "Ext.data.Model"
    , fields: [
        {name: "id", type: "string"}
        ,{name: "sender", type: "string"}
        ,{name: "recipient", type: "string"}
        ,{name: "amount", type: "float"}
        ,{name: "inputs"}
        ,{name: "outputs"}
    ]
});


Ext.define("TxInfo", {
    extend: "Ext.data.Model"
    , fields: [
        {name: "income"}
        , {name: "outcome"}
    ]
    , associations: [
        {
            type: "hasMany"
            , name: "income"
            , model: "Transaction"
        }, {
            type: "hasMany"
            , name: "outcome"
            , model: "Transaction"
        }
    ]
});

Ext.define('Ext.window.ResizedWindow',{
    extend: 'Ext.window.Window'
    , listeners: {
        resize: function(win, width, height, eOpts) {
            win.updateLayout();
        }
    }
});

let panelKeeper = [];

Ext.define('Ext.panel.ResizedPanel',{
    extend: 'Ext.panel.Panel'
    , listeners: {
        resize: function(panel, width, height, eOpts) {
            this.updateLayout();
        }
        , destroy: function(panel, eOpts) {
            let index = panelKeeper.indexOf(this);
            if(index !== -1) {
                panelKeeper.splice(index, 1);
            }
        }
        ,afterrender: function(panel, eOpts) {
            panelKeeper.push(this);
        }
    }
});

window.addEventListener("resize", function() {
    for(let index in panelKeeper) {
        panelKeeper[index].fireEvent("resize");
    }
});