Ext.onReady(function () {


    let txIncomeStore = Ext.create("Ext.data.Store", {
        storeId: "txIncome"
        , model: "Transaction"
        , autoLoad: false
    });

    let txOutcomeStore = Ext.create("Ext.data.Store", {
        storeId: "txOutcome"
        , model: "Transaction"
        , autoLoad: false
    });

    let txInfoStore = Ext.create("Ext.data.Store", {
        storeId: "txInfo"
        , model: "TxInfo"
        , autoLoad: true
        , proxy: {
            type: "ajax"
            , url: "/wallet/trans/get"
            , reader: {
                type: "json"
            }
        }
        , listeners: {
            load: function (self, records, successful, eOpts) {
                if(records && records[0]) {
                    let record = records[0].data;
                    txIncomeStore.removeAll();
                    if(record.income) {
                        for(let index in record.income) {
                            txIncomeStore.add(record.income[index]);
                        }
                    }
                    txOutcomeStore.removeAll();
                    if(record.outcome) {
                        for(let index in record.outcome) {
                            txOutcomeStore.add(record.outcome[index]);
                        }
                    }
                }
            }
        }
    });


    Ext.create("Ext.panel.ResizedPanel", {
        renderTo: "extBlock"
        , title: "Операции"
        , layout: "anchor"
        , items: [
            {
                xtype: "form"
                ,itemId: "loginForm"
                ,items: [
/*                    {
                        xtype: "fieldset"
                        //,border: false
                        ,items: [
                            {
                                xtype: "textfield"
                                ,itemId: "username"
                                ,name: "username"
                                ,fieldLabel: "Имя пользователя"
                                ,allowBlank: false
                            }
                            ,{
                                xtype: "textfield"
                                ,itemId: "password"
                                ,name: "password"
                                ,fieldLabel: "Пароль"
                                ,inputType: "password"
                                ,allowBlank: false
                            }, {
                                xtype: "button"
                                ,text: "Войти"
                                ,margin: "20 0 20 60"
                            }
                        ]
                    }*/
                    {
                        xtype: "fieldset"
                        ,border: false
                        , padding: "20 20 20 20"
                        ,items: [
                            {
                                xtype: "button"
                                , text: "Обновить"
                                , handler: function(btn) {
                                    txInfoStore.reload();
                                }
                            }
                        ]
                    },
                    {
                        xtype: "grid"
                        , title: "Входящие переводы"
                        , store: txIncomeStore
                        , columns: [
                            {
                                text: "ID"
                                , dataIndex: "id"
                                , flex: 2
                            },
                            {
                                text: "Отправитель"
                                , dataIndex: "sender"
                                , flex: 2
                            },
                            {
                                text: "Количество"
                                , dataIndex: "amount"
                                , flex: 1
                            }
                        ]
                    }, {
                        xtype: "grid"
                        , title: "Исходяшие переводы"
                        , store: txOutcomeStore
                        , columns: [
                            {
                                text: "ID"
                                , dataIndex: "id"
                                , flex: 2
                            },
                            {
                                text: "Получатель"
                                , dataIndex: "recipient"
                                , flex: 2
                            },
                            {
                                text: "Количество"
                                , dataIndex: "amount"
                                , flex: 1
                            }
                        ]
                    }
                ]
            }
        ]
    });
});