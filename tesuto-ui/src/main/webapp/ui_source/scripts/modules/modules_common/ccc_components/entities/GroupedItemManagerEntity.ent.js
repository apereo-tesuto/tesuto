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

    /**
     * The guts for grouping items results area. Use with the ccc-grouped-items directive
     */

    angular.module('CCC.Components').factory('GroupedItemManagerEntity', [

        '$rootScope',
        '$timeout',
        'ObservableEntity',

        function ($rootScope, $timeout, ObservableEntity) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var GroupedItemManagerEntity = function (entityData) {

                // default properties
                var defaults = {
                    items: [],
                    currentGrouping: '',    // can be false for no grouping
                    groupings: {}           // key value pairs, groupId: {groupConfig }
                    /**
                     * Example groupings
                     * {
                     *     grouping1: {
                     *         grouper: function () { return {id: '', title: ''} },
                     *         sortBy: function (group) {return group.title.toLowerCase(); }, (optional)
                     *         sortReverse: true (optional)
                     *     }
                     * }
                     */
                };

                var that = this;
                // we extend the observalbe class to add on event system to this class
                ObservableEntity.call(that);
                // we simply tack on merged properties during initialization
                $.extend(true, that, defaults, entityData, {});


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var groupedItems = [];

                var groupItems = function (items) {

                    var grouper = that.currentGrouping ? that.groupings[that.currentGrouping].grouper : false;

                    var groups = [];
                    var groupIndexMap = {};

                    // if we are currently grouping
                    if (grouper) {

                        _.each(items, function (item) {

                            var groupData = grouper(item);
                            var groupId = groupData.id;

                            // if we haven't indexed this group yet, do so now
                            if (groupIndexMap[groupId] === undefined) {

                                var newGroup = $.extend(true, {}, groupData, {items: []});
                                newGroup.items.push(item);

                                groups.push(newGroup);
                                groupIndexMap[groupId] = groups.length - 1;

                            // otherwise we already have a group to push into
                            } else {
                                groups[groupIndexMap[groupId]].items.push(item);
                            }
                        });

                        if (that.groupings[that.currentGrouping].sortBy) {

                            groups = _.sortBy(groups, that.groupings[that.currentGrouping].sortBy);
                            if (that.groupings[that.currentGrouping].sortReverse) {
                                groups = groups.reverse();
                            }
                        }

                    // otherwise no grouping so just create one group
                    } else {

                        groups = [{id: 0, items: items}];
                    }

                    return groups;
                };


                /*============ PUBLIC DATA METHODS ============*/

                that.setItems = function (items_in) {
                    that.items = items_in;
                    groupedItems = groupItems(that.items);
                    that.fireEvent('groupedItemsUpdated', [groupedItems]);
                };

                that.getGroupedItems = function () {
                    return groupedItems;
                };

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ ENTITY PASSBACK ============*/

            return GroupedItemManagerEntity;
        }
    ]);

})();
