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
     * The guts of a faceted search UI, works with ccc-facet-list directive
     */

    angular.module('CCC.Components').factory('FacetedSearchManagerClass', [

        '$rootScope',
        '$timeout',
        'ObservableEntity',

        function ($rootScope, $timeout, ObservableEntity) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            // default properties
            var defaults = {
                title: '',
                selectedFacets: {}, // a copy of what this entity emits when it calculates an update to selected facets
                facets: [
                    // { example, create a facet group with id and title and the field to group as well as a title and value transform
                    //     id: 'assessmentType',
                    //     title: 'Assessments',
                    //     multiSelect: false, // could be true if this facet is multi selectable, this means this field would be an array
                    //     fieldValueAndTitle: function (dataItem) { return {value: '', title: ''} --or-- return [{value: '', title: ''}, {value: '', title: ''}]  }
                    // }
                ]
            };


            /*============ CLASS DECLARATION ============*/

            var FacetedSearchManagerClass = function (entityData) {

                var that = this;
                // we extend the observalbe class to add on event system to this class
                ObservableEntity.call(that);
                // we simply tack on merged properties during initialization
                $.extend(true, that, defaults, entityData, {});


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var data = [];

                // this is the internal storage for which facets are selected
                var facetSelectionMap = that.selectedFacets || {};
                var facetsSelectedCount = 0;

                // filtered data based on the selection map
                var filteredData = [];

                // facetData is a summary of the facet values and counts (used primarily for ccc-facet-list directive)
                var facetData = [];

                // if we know counts ahead of time we can speed up processing when no value in a facet is picked
                var getFacetSelections = function () {

                    var facetSelections = {};
                    facetsSelectedCount = 0;

                    for (var facetKey in facetSelectionMap) {
                        if (facetSelectionMap.hasOwnProperty(facetKey)) {
                            facetSelections[facetKey] = [];
                            for (var valueKey in facetSelectionMap[facetKey]) {
                                if (facetSelectionMap[facetKey].hasOwnProperty(valueKey) && facetSelectionMap[facetKey][valueKey] === true) {
                                    facetsSelectedCount++;
                                    facetSelections[facetKey].push(valueKey);
                                }
                            }
                        }
                    }
                    return facetSelections;
                };

                var dataItemFacetInfoList;
                var dataItemFacetValuesSelectedMap;
                var isItemSelectedWithinFacet = function (dataItem, facet, selectedValues) {

                    dataItemFacetInfoList = getDataItemFacetInfoList(dataItem, facet);
                    dataItemFacetValuesSelectedMap = _.reduce(dataItemFacetInfoList, function (memo, dataItemFacetInfo) {
                        memo[dataItemFacetInfo.value + ''] = true;
                        return memo;
                    }, {});

                    var isSelected = true;

                    _.each(selectedValues, function (selectedValue) {
                        isSelected = isSelected && (dataItemFacetValuesSelectedMap[selectedValue + ''] === true);
                    });

                    return isSelected;
                };

                var filterByFacetSelection = function (dataItem, facetSelections) {

                    var selected = true;

                    // the item has to be valid in each facet category
                    // it's AND across facets and OR within a facet (which can have multple selections)
                    _.each(that.facets, function (facet) {

                        // if there are no selections in this group then we are okay
                        if (!facetSelections[facet.id] || facetSelections[facet.id].length === 0) {
                            selected = selected && true;
                        } else {
                            selected = selected && isItemSelectedWithinFacet(dataItem, facet, facetSelections[facet.id]);
                        }
                    });
                    return selected;
                };


                var filterData = function (data_in, facetSelections) {

                    return _.filter(data_in, function (dataItem) {
                        return filterByFacetSelection(dataItem, facetSelections);
                    });
                };

                // will return array [{value: <facetValueForDataItem>, title: <facetTitleForDataItem>}]
                var getDataItemFacetInfoList = function (dataItem, facet) {

                    if (!angular.isFunction(facet.fieldValueAndTitle)) {

                        throw new Error('FacetedSearchManagerEntity.fieldValueAndTitleNotAFunction');

                    } else {

                        var dataItemFacetInfoList = facet.fieldValueAndTitle(dataItem);

                        if (!_.isArray(dataItemFacetInfoList)) {
                            return [dataItemFacetInfoList];
                        } else {
                            return dataItemFacetInfoList;
                        }
                    }
                };


                var generateFacetData = function (dataList, facetSelections) {

                    var facetData = [];

                    // setup the initial facet data
                    facetData = [];
                    var facetCategoryMap = {};
                    _.each(that.facets, function (facet, index) {
                        var facetCategory = {
                            id: facet.id,
                            title: facet.title,
                            facetDataIndex: index,
                            values: [],
                            valueMap: {} // we create another lookup map by facet value id
                        };
                        facetData.push(facetCategory);
                        facetCategoryMap[facet.id] = facetCategory;
                    });

                    // now loop through each dataItem and add values and counts to the facet data
                    var dataItemCountMap;
                    _.each(dataList, function (dataItem) {

                        // each dataItem can only contribute one count per facet value, so we keep track
                        dataItemCountMap = {};

                        _.each(that.facets, function (facet) {

                            var dataItemFacetInfoList = getDataItemFacetInfoList(dataItem, facet);

                            dataItemCountMap[facet.id] = {};

                            // at this point we have an array [{value: <someValue>, title: <someTitle>}]
                            _.each(dataItemFacetInfoList, function (dataItemFacetInfo) {

                                // if this facet value doesn't exist create it
                                if (!facetCategoryMap[facet.id].valueMap[dataItemFacetInfo.value]) {

                                    var facetCategoryValue = {
                                        facetId: facet.id,
                                        id: dataItemFacetInfo.value,
                                        title: dataItemFacetInfo.title,
                                        count: 1,
                                        selected: facetSelections[facet.id + ''] && facetSelections[facet.id + ''].indexOf(dataItemFacetInfo.value + '') !== -1
                                    };

                                    facetCategoryMap[facet.id].values.push(facetCategoryValue);
                                    facetCategoryMap[facet.id].valueMap[dataItemFacetInfo.value] = facetCategoryValue;

                                    // mark this item as contributing to this facet category and value
                                    dataItemCountMap[facet.id][dataItemFacetInfo.value] = true;

                                // if it already exists just increase the count if this data item already hasn't contributed to the count
                                } else if (!dataItemCountMap[facet.id][dataItemFacetInfo.value]) {

                                    facetCategoryMap[facet.id].valueMap[dataItemFacetInfo.value].count++;

                                    // mark this item as contributing to this facet category and value
                                    dataItemCountMap[facet.id][dataItemFacetInfo.value] = true;
                                }

                            });

                        });
                    });

                    return facetData;
                };

                var getFacetConfig = function (facetId) {
                    return _.find(that.facets, function (facet) {
                        return facet.id === facetId;
                    });
                };

                var sortFacets = function (unSortedFacetData) {
                    _.each(unSortedFacetData, function (facet) {

                        var facetConfig = getFacetConfig(facet.id);

                        if (facetConfig.sortBy) {

                            facet.values = _.sortBy(facet.values, facetConfig.sortBy);
                            if (facetConfig.sortReverse) {
                                facet.values = facet.values.reverse();
                            }
                        }
                    });
                    return unSortedFacetData;
                };

                // any time data or facet selection changes, this should be run
                var updateFilterDataAndFacetData = function () {

                    // let's only calculate this once for performance reasons
                    var facetSelections = getFacetSelections();

                    // filter the data based on current facet selections
                    filteredData = filterData(data, facetSelections);

                    // populate the facetData object that has information for the UI
                    var unSortedFacetData = generateFacetData(filteredData, facetSelections);

                    facetData = sortFacets(unSortedFacetData);

                    that.fireEvent('filteredDataUpdated', [filteredData]);
                    that.fireEvent('facetDataUpdated', [facetData, facetSelectionMap, facetsSelectedCount]);
                };


                /*============ PUBLIC DATA METHODS ============*/

                that.setData = function (data_in) {

                    // store a local reference to the original data
                    data = data_in;
                    updateFilterDataAndFacetData();
                };

                that.getFilteredData = function () {
                    return filteredData;
                };


                /*============ PUBLIC FACET METHOS ============*/

                that.getSelectedFacetsCount = function () {
                    return facetsSelectedCount;
                };

                that.getFacetData = function () {
                    return facetData;
                };

                that.clearAllFacets = function () {
                    facetSelectionMap = {};
                    updateFilterDataAndFacetData();
                };

                that.selectFacet = function (facetGroupId, facetId) {
                    console.log('NOT IMPLEMENTED');
                };

                that.unSelectFacet = function (facetGroupId, facetId) {
                    console.log('NOT IMPLEMENTED');
                };

                that.toggleFacet = function (facetId, facetValueId) {

                    facetId = facetId + '';
                    facetValueId = facetValueId + '';

                    if (!facetSelectionMap[facetId]) {
                        facetSelectionMap[facetId] = {};
                    }

                    if (!facetSelectionMap[facetId][facetValueId]) {
                        facetSelectionMap[facetId][facetValueId] = true;
                    } else {
                        facetSelectionMap[facetId][facetValueId] = false;
                    }
                    updateFilterDataAndFacetData();
                };

                that.selectAllFacetsInGroup = function (facetGroupId) {
                    console.log('NOT IMPLEMENTED');
                };

                that.deselectAllFacetsInGroup = function (facetGroupId) {
                    console.log('NOT IMPLEMENTED');
                };

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ ENTITY PASSBACK ============*/

            return FacetedSearchManagerClass;
        }
    ]);

})();
