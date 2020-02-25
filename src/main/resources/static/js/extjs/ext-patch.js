/**
 * common extjs overrides
 */
Ext.apply(Ext,{
    BLANK_IMAGE_URL:"js/ext-4/s.gif"
});
Ext.apply(Ext.Ajax,{
    timeout:6*60*1000
});
Ext.apply(Ext.data.Connection,{
    timeout:6*60*1000
});
Ext.apply(Ext.Loader,{
    disableCaching:true
});
Ext.apply(Ext.form.Basic,{
    timeout:6*60
});
var piP=/^\d{6}$/;
Ext.apply(Ext.form.field.VTypes, {
    //  vtype validation function
    postindex:function(val, field) {
        return piP.test(val);
    },
    postindexText:'Неверный формат индекса. Пример: 443000',
    postindexMask:/\d/i
});
Ext.apply(Ext.form.field.VTypes, {
    //  vtype validation function
    numeric:function(val, field) {
        return /^\d$/.test(val);
    },
    numericText:'Неверный формат числа. Введите целое число',
    numericMask:/\d/i
});
Ext.apply(Ext.form.field.VTypes, {
    //  vtype validation function
    numerics:function(val, field) {
        return /^\d*$/.test(val);
    },
    numericsText:'Неверный формат числа. Введите целое число',
    numericsMask:/\d/i
});
 Ext.apply(Ext.form.field.VTypes, {
    //  vtype validation function
    currency:function(val, field) {
        return /[0-9]\d{0,2}(\,\d{1,2})?%?$/.test(val);
    },
    currencyText:'Неверный формат числа. Число должно иметь вид 253,96',
    currencyMask:/[\d\,]/i
});
Ext.define('Ext.form.SubmitFix', {
    override: 'Ext.ZIndexManager',
    register : function(comp) {
        var me = this,
            compAfterHide = comp.afterHide;

        if (comp.zIndexManager) {
            comp.zIndexManager.unregister(comp);
        }
        comp.zIndexManager = me;

        me.list[comp.id] = comp;
        me.zIndexStack.push(comp);

        // Hook into Component's afterHide processing
        comp.afterHide = function() {
            compAfterHide.apply(comp, arguments);
            me.onComponentHide(comp);
        };
    },

    /**
     * Unregisters a {@link Ext.Component} from this ZIndexManager. This should not
     * need to be called. Components are automatically unregistered upon destruction.
     * See {@link #register}.
     * @param {Ext.Component} comp The Component to unregister.
     */
    unregister : function(comp) {
        var me = this,
            list = me.list;

        delete comp.zIndexManager;
        if (list && list[comp.id]) {
            delete list[comp.id];

            // Relinquish control of Component's afterHide processing
            delete comp.afterHide;
            Ext.Array.remove(me.zIndexStack, comp);

            // Destruction requires that the topmost visible floater be activated. Same as hiding.
            me._activateLast();
        }
    }
});
Ext.define('Ext.ux.ItemSelectorBugFix', {
    override: 'Ext.ux.ItemSelector'
    ,onBindStore: function(store, initial) {
        var me = this;

        if (me.fromField) {
            me.fromField.store.removeAll()
            me.toField.store.removeAll();

            // Add everything to the from field as soon as the Store is loaded
            if (store.getCount()) {
                me.populateFromStore(store);
            } else {
            // On dynamic store load this part throw double store population and as result - exception on binded view
            //   me.store.on('load', me.populateFromStore, store);
            }
        }
    }
});