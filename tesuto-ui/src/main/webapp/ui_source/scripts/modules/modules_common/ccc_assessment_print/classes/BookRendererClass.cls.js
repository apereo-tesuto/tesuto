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

    angular.module('CCC.AssessmentPrint').factory('BookRendererClass', [

        '$q',
        '$timeout',

        function ($q, $timeout) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            // A Page helper class to abstract some of the page level logic out
            var Page = function (configs_in) {

                var defaults = {
                    target: null,
                    header: false,
                    footer: false,
                    pageNum: 0,                 // should be passed in
                    newsPaperColumns: false,    // should we render two columns for newsPaperColumn style printing?
                    pageRowPostProcessors: []   // an array of functions that get passed the pageRowInstance and must return a promise
                };

                var that = this;
                $.extend(true, that, defaults, configs_in || {});

                /*============= COERCE SOME VALUES ============*/

                // let's add at least one pageRowPostProcessor to ensure the dom has settled before measuring
                if(that.pageRowPostProcessors.length === 0) {

                    that.pageRowPostProcessors = [];
                    that.pageRowPostProcessors.push(function () {
                        return $q.when(true);
                    });
                }


                /*============= PRIVATE VARIABLES / METHODS ===========*/

                var HEADER_MARGIN = 30; // margin below header before we can render content
                var FOOTER_MARGIN = 15; // margin above footer before we can render content

                var headerElement;
                var footerElement;


                /*============= PUBLIC PROPERTIES / METHODS ===========*/

                that.currentPageRow = 0;

                that.element = $([
                    '<div class="ccc-book-render-page">',
                        '<div class="ccc-book-render-page-inner">',
                            '<div class="ccc-book-render-page-header"></div>',
                            '<div class="ccc-book-render-page-content container-fluid"></div>',
                            '<div class="ccc-book-render-page-footer"></div>',
                        '</div>',
                    '</div>'
                ].join(''));

                that.contentElement = that.element.find('.ccc-book-render-page-inner');

                headerElement = that.element.find('.ccc-book-render-page-header');
                footerElement = that.element.find('.ccc-book-render-page-footer');

                if (that.newsPaperColumns) {

                    that.currentColumn = 1;

                    var nestedColumns = $([
                        '<div class="row">',
                            '<div class="ccc-book-render-page-column-one col-xs-6"></div>',
                            '<div class="ccc-book-render-page-column-two col-xs-6"></div>',
                        '</div>'
                    ].join(''));

                    that.element.find('.ccc-book-render-page-content').append(nestedColumns);
                }

                that.pristine = true;

                that.isContentFitting = function (pageRowInstance) {

                    var pageRowPostProcessPromiseChain = $q.when(true);

                    var attachPageRowPromise = function (postProcessor) {
                        pageRowPostProcessPromiseChain = pageRowPostProcessPromiseChain.then(function () {

                            var reflowDeferred = $q.defer();

                            // once the postProcessing is done, also query the dom in a way that forces it to solidify heights of all elements for proper measuring
                            postProcessor(pageRowInstance).then(function () {

                                $timeout(function () {
                                    pageRowInstance.element.offsetLeft = pageRowInstance.element.offsetLeft;
                                    reflowDeferred.resolve();
                                });
                            });

                            return reflowDeferred.promise;
                        });
                    };

                    _.each(that.pageRowPostProcessors, function (postProcessor) {
                        attachPageRowPromise(postProcessor);
                    });

                    var pageHeight = that.contentElement.height();
                    var pageHeightWithPadding = that.contentElement.outerHeight();
                    var pageVerticalPadding = pageHeightWithPadding - pageHeight;

                    // once the post processors are done, we can accurately measure the fit
                    return pageRowPostProcessPromiseChain.then(function () {

                        var pageRowOuterHeight = pageRowInstance.element.outerHeight();
                        var pageRowTopOffset = pageRowInstance.element.offset().top - that.contentElement.offset().top;

                        // the row height plus it's vertical offset must not be larger than the page height
                        return (pageRowOuterHeight + pageRowTopOffset) <= (pageHeight + pageVerticalPadding / 2);
                    });
                };

                that.addPageRow = function (pageRowInstance) {

                    // NOTE: you an uncomment the following line when working offline and images can't load
                    //pageRowInstance.element.find('img, object').remove();

                    // increment the row counter
                    that.currentPageRow++;

                    // get proper dimensions
                    var headerHeight = headerElement.outerHeight() + HEADER_MARGIN;
                    var footerHeight = footerElement.outerHeight() + FOOTER_MARGIN;

                    that.element.find('.ccc-book-render-page-inner').css('padding-top', headerHeight);
                    that.element.find('.ccc-book-render-page-inner').css('padding-bottom', footerHeight);

                    // normal rendering mode
                    if (!that.newsPaperColumns) {

                        that.element.find('.ccc-book-render-page-content').append(pageRowInstance.element);

                        // we return a check on if the content actually fits
                        return that.isContentFitting(pageRowInstance);

                    // newsPaperColumns rendering mode
                    } else {

                        var currentColumnElement;
                        if (that.currentColumn === 1) {
                            currentColumnElement = that.element.find('.ccc-book-render-page-column-one');
                        } else {
                            currentColumnElement = that.element.find('.ccc-book-render-page-column-two');
                        }

                        currentColumnElement.append(pageRowInstance.element);

                        // in newsPaperColumn mode we check internally and switch columns
                        return that.isContentFitting(pageRowInstance).then(function (isContentFitting) {

                            if (!isContentFitting) {

                                // automatically switch to column 2
                                if (that.currentColumn === 1) {

                                    pageRowInstance.element.detach();
                                    that.currentColumn = 2;
                                    that.element.find('.ccc-book-render-page-column-two').append(pageRowInstance.element);

                                    return that.isContentFitting(pageRowInstance);

                                // if we are already in column 2, then this page is full
                                } else {
                                    return false;
                                }

                            } else {
                                return true;
                            }
                        });
                    }
                };

                that.removePageRow = function (pageRowInstance) {

                    // decrement the row counter and remove the row element
                    that.currentPageRow++;
                    pageRowInstance.element.remove();
                };

                that.allowVerticalExpansion = function () {
                    that.element.find('.ccc-book-render-page-inner').css('height', 'auto').css('min-height', 0);
                };


                /*============= INITIALIZATION ===========*/

                // add the header and footer
                if (that.header) {
                    headerElement.append(that.header(that.pageNum));
                }
                if (that.footer) {
                    footerElement.append(that.footer(that.pageNum));
                }

                // add the page to the target
                that.target.append(that.element);
            };

            // A PageRow helper class to abstract out pageRow level logic
            var PageRow = function (configs_in) {

                var defaults = {
                    columns: []
                };

                var thatPageRow = this;
                $.extend(true, thatPageRow, defaults, configs_in || {});

                /*============= PRIVATE VARIABLES / METHODS ===========*/

                // create a flat array of all items for quick scanning
                var flatItemList;
                var generateFlatItemList = function () {
                    flatItemList = [];
                    for (var i=0; i < thatPageRow.columns.length; i++) {
                        for (var j=0; j < thatPageRow.columns[i].length; j++) {
                            flatItemList.push({
                                columnNumber: i,
                                rowNumber: j,
                                item: thatPageRow.columns[i][j]
                            });
                        }
                    }
                };


                /*============= PUBLIC PROPERTIES / METHODS===========*/

                thatPageRow.element = $('<div class="ccc-book-render-page-row row"></div>');

                thatPageRow.render = function () {

                    // IMPORTANT: first rip out all rendered elements from the dom (before it is cleared)
                    // This allows them to be placed again in the dom without messing up preprocessors
                    // If you allow it to be cleared using html('') before detaching, then you will lose the ability
                    // to track with objects, images, and math have finished rendering
                    _.each(flatItemList, function (itemData) {
                        if (itemData.item.element) {
                            itemData.item.element.detach();
                        }
                    });

                    // first clear the area
                    thatPageRow.element.html('');

                    // next render columns
                    var columnElements = [];
                    var columnClass = thatPageRow.columns.length === 1 ? 'col-xs-12' : 'col-xs-6';

                    for (var i=0; i < thatPageRow.columns.length; i++) {
                        var columnElement = $('<div class="ccc-book-render-page-row-column"></div>').addClass(columnClass);
                        columnElements.push(columnElement);
                        columnElement.appendTo(thatPageRow.element);
                    }

                    // next iterate through each item, render it and tag it as rendered = true
                    _.each(flatItemList, function (itemData) {

                        // render the element if it hasn't been yet
                        if (!itemData.item.element) {
                            itemData.item.element = $(itemData.item.template);
                        }

                        itemData.item.rendered = true;
                        columnElements[itemData.columnNumber].append(itemData.item.element);
                    });
                };

                thatPageRow.unrenderItemById = function (itemId) {

                    _.each(flatItemList, function (flatItemData) {
                        if (flatItemData.item.id === itemId) {
                            flatItemData.item.rendered = false;
                            flatItemData.item.element.detach();
                        }
                    });
                };

                thatPageRow.mergeColumns = function () {

                    if (thatPageRow.columns.length > 1) {
                        thatPageRow.columns[0] = thatPageRow.columns[0].concat(thatPageRow.columns[1]);
                        thatPageRow.columns.pop();
                    }

                    // after restructuring the columns, update the flat item list and re-render
                    generateFlatItemList();
                    thatPageRow.render();
                };

                // scan through all unrendered items, copy them to a new pageRow
                thatPageRow.getNewPageRowWithUnrenderedItems = function () {

                    var newRowObj = {
                        columns: []
                    };

                    var unrenderedFlatList = [];

                    // need to remove the items from the columns
                    _.each(thatPageRow.columns, function (columnItems, columnIndex) {

                        // add a column if it doesn't already exists
                        if (!newRowObj.columns[columnIndex]) {
                            newRowObj.columns.push([]);
                        }

                        _.each(columnItems, function (item) {

                            if(!item.rendered) {
                                newRowObj.columns[columnIndex].push(item);
                                // keep track of all those we need to go back and remove from the columns
                                unrenderedFlatList.push(item);
                            }
                        });
                    });

                    // now that we have a new list of unrendered items to break apart from the original row
                    // we need to remove references in the columns
                    _.each(unrenderedFlatList, function (unrenderedItem) {

                        // TODO, use for loops with breaks to reduce number of iterations when we find it
                        _.each(thatPageRow.columns, function (columnItems, columnIndex) {
                            for (var i=0; i < columnItems.length; i++) {

                                if(columnItems[i].id === unrenderedItem.id) {
                                    columnItems.splice(i, 1);
                                    break;
                                }
                            }
                        });
                    });

                    // now that the columns within this pageRow are clean, generate a new flat list (lookup)
                    generateFlatItemList();

                    // then lastly return the new PageRow built from all unrendered items in this page row
                    return new PageRow(newRowObj);
                };

                thatPageRow.eachItem = function (callBack) {

                    _.each(flatItemList, function (flatItemData, flatItemDataIndex) {
                        callBack(flatItemData, flatItemDataIndex);
                    });
                };


                /*============= INITIALIZATION =============*/

                generateFlatItemList();
                thatPageRow.render();
            };


            /*============ CLASS DECLARATION ============*/

            var BookRendererClass = function (targetElement, configs_in) {

                var defaults = {

                    fontSize: 12,                   // in pt size
                    pageHeight: 27,                 // in centemeters (this is ignored if naturalPageBreaks is true)

                    startingPageNumber: 1,          // starting point for page numbering

                    header: false,                  // pass in a function that returns a template to be rendered as a header on every page
                    footer: false,                  // pass in a function that returns a template to be rednered as a footer on every page

                    newsPaperColumns: false,        // render top to bottom left to right

                    pageRowPostProcessors: []       // an array of functions that get passed pageRowInstances and return a promise when any post processing/loading is complete
                };

                // merge in the defaults onto the instance
                var that = this;
                $.extend(true, that, defaults, configs_in || {});


                /*=============== COERCE SOME INPUT VALUES =============*/

                // make sure it is a jquery object so coerce it if it isn't
                that.element = $(targetElement);


                /*=============== CONSTANTS ============*/

                var MAX_FONT_SIZE_ALLOWED_FOR_TWO_COLUMNS = 16;


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                var currentPage;

                var forceOneColumnForAllItems = that.fontSize > MAX_FONT_SIZE_ALLOWED_FOR_TWO_COLUMNS;

                var currentPageNum = 0;
                var getNewPage = function () {

                    currentPageNum++;

                    return new Page({
                        target: that.element,
                        pageNum: that.startingPageNumber + (currentPageNum - 1),
                        header: that.header,
                        footer: that.footer,
                        newsPaperColumns: that.newsPaperColumns,
                        pageRowPostProcessors: that.pageRowPostProcessors
                    });
                };

                var pageRowElementFitsWithinPage = function (pageRowElement, pageElement) {
                    var pageHeight = pageElement.height();
                    var pageHeightWithPadding = pageElement.outerHeight();
                    var pageVerticalPadding = pageHeightWithPadding - pageHeight;

                    var pageRowElementOuterHeight = pageRowElement.outerHeight();
                    var pageRowElementTopOffset = pageRowElement.offset().top - (pageElement.offset().top - (pageVerticalPadding / 2));

                    // the row item height plus it's vertical offset relative to the page must not be larger than the page height
                    return (pageRowElementOuterHeight + pageRowElementTopOffset) <= pageHeight;
                };

                var pageRowElementIsTallerThanPageHeight = function (pageRowElement, pageElement) {
                    var pageHeight = pageElement.height();
                    // here we pass in true because margin will indeed affect the page row total height
                    return pageRowElement.outerHeight(true) > pageHeight;
                };


                // render the pageRow
                var renderPageRow = function (pageRow) {

                    var deferred = $q.defer();

                    // NOTE: The following logic enforce that two columns items that are related should render appropriatly

                    currentPage.addPageRow(pageRow).then(function (isContentFitting) {

                        // ** If the whole pageRow did not fit we need to break off the pieces that did not fit
                        if (!isContentFitting) {

                            // ** IF ANY ITEM HEIGHT IS GREATER THAN THE TOTAL PAGE HEIGHT, COLLAPSE INTO ONE COLUMN AND START OVER
                            if (pageRow.columns.length > 1) {

                                var eachItemShorterThanPageHeight = true;
                                pageRow.eachItem(function (pageRowItemData) {
                                    eachItemShorterThanPageHeight = eachItemShorterThanPageHeight && !pageRowElementIsTallerThanPageHeight(pageRowItemData.item.element, currentPage.contentElement);
                                });

                                if (!eachItemShorterThanPageHeight) {

                                    // remove the page row
                                    currentPage.removePageRow(pageRow);

                                    // restructure the page row items
                                    pageRow.mergeColumns();

                                    // re-add it and try again returning a new promise
                                    return renderPageRow(pageRow).then(function () {
                                        deferred.resolve();
                                    });
                                }
                            }

                            var firstItemInAColumnBrokeOut = false;
                            pageRow.eachItem(function (pageRowItemData) {

                                // if it doesn't fit within the current page unrender it
                                if (!pageRowElementFitsWithinPage(pageRowItemData.item.element, currentPage.contentElement)) {
                                    if (pageRowItemData.rowNumber === 0) {
                                        firstItemInAColumnBrokeOut = true;
                                    }
                                }
                            });

                            // ** DID A FIRST ITEM BREAK OUT OF THE PAGE ?
                            // THEN WE NEED TO SIMPLY MOVE TO THE NEXT PAGE
                            if (firstItemInAColumnBrokeOut) {

                                // if is the first item and it doesn't fit and it's not the first pageRow item
                                if (currentPage.currentPageRow !== 1) {

                                    // remove the page row and let's start on a new page
                                    currentPage.removePageRow(pageRow);
                                    currentPage = getNewPage();

                                    // re-add it and try again returning a new promise
                                    renderPageRow(pageRow).then(function () {
                                        deferred.resolve();
                                    });

                                    return deferred.promise;
                                }
                            }

                            // ** DID A NON-FIRST ITEM BREAK OUT OF THE PAGE -> Rip it out and create a new pageRow with remaining items
                            var itemIdsThatNeedUnrendering = [];
                            pageRow.eachItem(function (pageRowItemData, pageRowItemIndex) {

                                // if it doesn't fit within the current page unrender it
                                // TODO: optimize, if one item in a column doesn't fit... the rest under it won't either no need to check

                                var itemFits = pageRowElementFitsWithinPage(pageRowItemData.item.element, currentPage.contentElement);

                                if (!itemFits) {

                                    // Allowing the first item to render even if it doesn't fit avoids an infinite loop
                                    if (currentPage.currentPageRow !== 1 || currentPage.currentPageRow === 1 && pageRowItemIndex !== 0) {

                                        itemIdsThatNeedUnrendering.push(pageRowItemData.item.id);

                                    // WARNING : KNOWN ISSUE : In this situation, the first item in the first page row doesn't fit in a single column
                                    // there is nothing we can really do here without significant effort to split the markup in half which could lead to undesired results
                                    // so we allow the page to expand vertically, it will print overlapping multiple pages and the page numbering will spill onto the last page
                                    } else {

                                        currentPage.allowVerticalExpansion();
                                    }
                                }
                            });

                            for(var i=0; i < itemIdsThatNeedUnrendering.length; i++) {
                                pageRow.unrenderItemById(itemIdsThatNeedUnrendering[i]);
                            }

                            // ** NOW GET ALL UNRENDERED ITEMS IN A NEW PAGE ROW OBJECT AND RENDER IT ON THE NEXT PAGE
                            var unrenderedPageRow = pageRow.getNewPageRowWithUnrenderedItems();

                            // all the unrendered items will go on the next page
                            currentPage = getNewPage();

                            // re-add it and try again returning a new promise
                            renderPageRow(unrenderedPageRow).then(function () {
                                deferred.resolve();
                            });

                        // if everything fit on this page, then just resolve
                        } else {
                            deferred.resolve();
                        }
                    });

                    return deferred.promise;
                };

                var renderItemRow = function (itemRow) {

                    // right now we can only have 1 or 2 columns
                    var columnsForRendering = (itemRow.columns.length === 1 || forceOneColumnForAllItems) ? 1 : 2;

                    var pageRow = new PageRow(itemRow);

                    // collapse collumns if we can only have 1 regardless of how many columns where configured (because of book settings etc...)
                    if (columnsForRendering === 1 && itemRow.columns.length === 2) {
                        pageRow.mergeColumns();
                    }

                    return renderPageRow(pageRow);
                };


                /*=============== PUBLIC PROPERTIES =============*/

                that.clear = function () {
                    that.element.html('');
                };

                // This is the expected structure of the itemList, an array of items that are configured in rows with columns of 1 or 2 columns, each column having an array of templates to be rendered
                // You can give an item a id and reference that id in another item using the associateContent property and the value of the id
                // This will ensure that in two column mode, if an item is not on the same page as the associatedContent, a corresponding message will appear
                // var itemList = [
                //     {
                //         rows: [
                //             {
                //                 columns: [
                //                     [   // column 1
                //                         {
                //                             id: 1,                // an assessment wide unique ID to be used by other items as a reference
                //                             template: $('<div></div>')   // template should be a dom string or possibly jQuery wrapped element
                //                         }
                //                     ],
                //                     [   // column 2
                //                         {
                //                              associatedId: 1,
                //                              template: $('<div></div>')
                //                         },
                //                         {item template},
                //                         {item template},
                //                         {item template}
                //                     ]
                //                 ]
                //             }
                //         ]
                //     }
                // ];

                that.setStartingPageNumber = function (newStartingPageNumber) {
                    that.startingPageNumber = newStartingPageNumber;
                };

                // RULES THAT GOVERN LAYOUT DECISIONS
                // 1) IF ANY SINGLE ITEM IN A MULTI-COLUMN LAYOUT IS TALLER THAN PAGE HEIGHT : FORCE ENTIRE ITEM INTO SINGLE COLUMN
                // 2) IF A ROW HAS TWO COLUMNS AND IF EITHER OF THE FIRST ITEMS IN EITHER COLUMN DO NOT FIT ON A PAGE, TRY THE NEXT PAGE, IF THAT DOESN'T WORK... FORCE SINGLE COLUMN

                that.renderItems = function (itemList) {

                    that.clear();
                    currentPage = getNewPage();

                    var itemRowPromiseChain = $q.when(true);

                    var attachItemRowPromise = function (itemRow) {

                        itemRowPromiseChain = itemRowPromiseChain.then(function () {
                            return renderItemRow(itemRow);
                        });
                    };

                    _.each(itemList, function (item) {
                        _.each(item.rows, function (itemRow) {
                            attachItemRowPromise(itemRow);
                        });
                    });

                    return itemRowPromiseChain.then(function () {
                        return currentPageNum;
                    });
                };


                /*=============== PUBLIC METHODS =============*/

                /*=============== INITIALIZTION =============*/

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return BookRendererClass;
        }
    ]);

})();
