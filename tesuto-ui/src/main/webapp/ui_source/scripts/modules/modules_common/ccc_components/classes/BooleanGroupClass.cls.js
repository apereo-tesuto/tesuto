/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
(function () {

    /*============ PRIVATE STATIC FOR BOTH CLASSES ==========*/

    var booleanItemId = 0;
    var getBooleanItemId = function () {
        return booleanItemId++;
    };


    /*============ BOOLEAN GROUP CLASS ==========*/

    angular.module('CCC.Components').factory('BooleanGroupClass', [

        '$q',
        'ObservableEntity',
        'BooleanItemClass',

        function ($q, ObservableEntity, BooleanItemClass) {

            /*============ PRIVATE STATIC CONSTANTS ============*/

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var BooleanGroupClass = function (configs) {

                /*============ CLASS DEFAULT CONFIGS ============*/

                var defaults = {

                    // you can initialize this with the following simple data structures and it will auto hydrate
                    booleanItems: [
                        // {
                        //     data: {},
                        //     directive: '<ccc-competency-group-list-item competency-group="data"></ccc-competency-group-list-item>',
                        //     operator: 'and'
                        // },
                        // {
                        //     data: {},
                        //     directive: '<ccc-competency-group-list-item competency-group="data"></ccc-competency-group-list-item>',
                        //     operator: 'and'
                        // },
                        // BooleanGroup,
                    ],

                    operator: null,

                    // a function that needs to be overriden and should return a promise with the next items data
                    getItem: function (booleanGroupInstance) {
                        return $q.when({
                            data: {},
                            directive: '<div>No get item method defined</div>'
                        });
                    },

                    // a function that needs to be overriden and should return a promise with the next array of items, could be one or more items
                    getGroup: function (booleanGroupInstance) {
                        return $q.when({
                            booleanItems: [{
                                data: {},
                                directive: '<div>No get group method defined</div>'
                            }],
                            operator: null
                        });
                    },

                    itemDeletedEvent: false, // an event to listen to to tell if an item wants to be deleted
                    itemClickedEvent: false, // an event to listen to to tell if an item was clicked

                    // custom method you can pass in to serialize data into a boolean string reprentation
                    dataSerializer: function (data) {
                        return 'no serializer set';
                    }
                };

                /*============ MERGE IN DEFAULTS ============*/

                var that = this;
                // we extend the observalbe class to add on event system to this class
                ObservableEntity.call(that);
                $.extend(true, that, defaults, configs || {});

                // give it a uniqueid
                that.booleanItemId = getBooleanItemId();

                // set loading to true
                that.loading = true;


                /*=============== PRIVATE VARIABLES AND METHODS ============*/

                var hydrateBooleanItems = function () {

                    var booleanItemsReference = that.booleanItems;
                    that.booleanItems = [];

                    _.each(booleanItemsReference, function (booleanItemData, index) {

                        // if its another group
                        if (booleanItemData.booleanItems) {

                            that.addGroup(booleanItemData, true);

                        // if its an item
                        } else {
                            that.addItem(booleanItemData, true);
                        }
                    });

                    that.loading = false;
                };

                var setLastItemOperator = function () {

                    // when we add a new item we need to make sure the previous item has it's operator set
                    if (that.booleanItems.length) {
                        var lastItem = that.booleanItems[that.booleanItems.length - 1];
                        if (lastItem.operator === null) {
                            lastItem.operator = 'and';
                        }
                    }
                };


                /*=============== PUBLIC METHODS =============*/

                that.setBooleanItems = function (booleanItems) {
                    that.booleanItems = booleanItems;
                    hydrateBooleanItems();
                };

                that.removeItem = function (bId) {

                    // search through my children and remove
                    var foundIndex = -1;
                    _.find(that.booleanItems, function (booleanItem, foundIndexCandidate) {
                        if (bId === booleanItem.booleanItemId) {
                            foundIndex = foundIndexCandidate;
                        }
                        return bId === booleanItem.booleanItemId;
                    });

                    if (foundIndex !== undefined) {
                        that.booleanItems.splice(foundIndex, 1);
                    }

                    // when we add a new item we need to make sure the last item has no operator
                    if (that.booleanItems.length) {

                        var lastItem = that.booleanItems[that.booleanItems.length - 1];
                        lastItem.operator = null;

                    } else {
                        that.fireEvent('empty');
                    }

                    that.fireEvent('structureChange');
                };

                // used internally by directive
                that._addItem = function (callBack) {

                    callBack = callBack || $.noop;

                    that.loading = true;
                    that.itemLoading = true;

                    that.getItem(that).then(function (newItemData) {

                        that.addItem(newItemData);
                        callBack(newItemData);

                    }).finally(function () {
                        that.loading = false;
                        that.itemLoading = false;
                    });
                };

                that.addItem = function (itemConfigs, hydrating) {

                    setLastItemOperator();

                    var newItem = new BooleanItemClass(itemConfigs);
                    that.booleanItems.push(newItem);

                    // bubble up structure changes
                    newItem.addListener('operatorChange', function () {
                        that.fireEvent('structureChange');
                    });

                    if (!hydrating) {
                        that.fireEvent('structureChange');
                    }
                };

                // used internally by directive
                that._addGroup = function (callBack) {

                    callBack = callBack || $.noop;

                    that.loading = true;
                    that.groupLoading = true;

                    that.getGroup(that).then(function (newGroupData) {

                        that.addGroup(newGroupData);
                        callBack(newGroupData);

                    }).finally(function () {
                        that.loading = false;
                        that.groupLoading = false;
                    });
                };

                that.addGroup = function (groupConfigs, hydrating) {

                    // pass along some configured methods to each nested group
                    groupConfigs.getGroup = that.getGroup;
                    groupConfigs.getItem = that.getItem;
                    groupConfigs.itemDeletedEvent = that.itemDeletedEvent;
                    groupConfigs.itemClickedEvent = that.itemClickedEvent;
                    groupConfigs.deleteItem = that.deleteItem;
                    groupConfigs.dataSerializer = that.dataSerializer;

                    setLastItemOperator();
                    var newBooleanGroup = new BooleanGroupClass(groupConfigs);
                    that.booleanItems.push(newBooleanGroup);

                    // bubble up structure changes
                    newBooleanGroup.addListener('structureChange', function () {
                        that.fireEvent('structureChange');
                    });

                    // if this group is ever empty, we should just remove it
                    newBooleanGroup.addListener('empty', function () {
                        that.removeItem(newBooleanGroup.booleanItemId);
                    });

                    // ignore if we are building out initially
                    if (!hydrating) {
                        that.fireEvent('structureChange');
                    }

                    return newBooleanGroup;
                };

                that.changeOperator = function (newOperator) {

                    that.operator = newOperator;
                    that.fireEvent('structureChange');
                };

                // recursively build out a simpler structure
                that.serialize = function () {

                    var serializedObject = [];

                    _.each(that.booleanItems, function (booleanItem) {

                        if (booleanItem instanceof BooleanGroupClass) {

                            serializedObject.push(booleanItem.serialize());

                            if (booleanItem.operator !== null) {
                                serializedObject.push(booleanItem.operator);
                            }

                        } else if (booleanItem instanceof BooleanItemClass) {

                            serializedObject.push(that.dataSerializer(booleanItem.data));

                            if (booleanItem.operator !== null) {
                                serializedObject.push(booleanItem.operator);
                            }
                        }
                    });

                    return serializedObject;
                };


                /*=============== INITIALIZTION =============*/

                hydrateBooleanItems();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ STATIC LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return BooleanGroupClass;
        }
    ]);


    /*============ BOOLEAN ITEM CLASS ==========*/

    angular.module('CCC.Components').factory('BooleanItemClass', [

        '$rootScope',
        'ObservableEntity',

        function ($rootScope, ObservableEntity) {

            /*============ PRIVATE STATIC CONSTANTS ============*/

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var BooleanItemClass = function (configs) {

                /*============ CLASS DEFAULT CONFIGS ============*/

                var defaults = {

                    booleanItemId: null,    // mandatory

                    data: {},
                    directive: null,        // mandatory

                    operator: null
                };


                /*============ MERGE IN DEFAULTS ============*/

                var that = this;
                // we extend the observalbe class to add on event system to this class
                ObservableEntity.call(that);
                $.extend(true, that, defaults, configs || {});

                that.booleanItemId = getBooleanItemId();


                /*=============== PRIVATE VARIABLES AND METHODS ============*/


                /*=============== PUBLIC METHODS =============*/

                that.changeOperator = function (newOperator) {
                    that.operator = newOperator;
                    that.fireEvent('operatorChange');
                };

                /*=============== INITIALIZTION =============*/
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ STATIC LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return BooleanItemClass;
        }
    ]);


})();
