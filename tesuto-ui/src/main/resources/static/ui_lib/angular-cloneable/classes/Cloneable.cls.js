(function () {

    /*============= EXTEND ANGULAR (1.5.5) ============*/

    angular.isCloneable = function (item) {
        return angular.isObject(item) ? item._AngularCloneable === true : false;
    };

    /**
     * A base class that adds a clone method that recursively makes a deep copy and re-instantiates objects that have also extended this class
     */
    angular.module('AngularCloneable').factory('Cloneable', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var Cloneable = function () {

                var that = this;


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                // Search all nested objects that are also cloneable, we should clone them too
                // otherwise recursively return and replace cloneable objects
                // NOTE: passing in alreadyCloned avoids infinite loops
                var reinstantiateCloneableItems = function (item, alreadyCloned) {

                    // first we check if it is cloneable
                    if (angular.isCloneable(item) && !alreadyCloned) {

                        return item.clone();

                    // next it could be an array
                    } else if (angular.isArray(item)) {

                        angular.forEach(item, function (arrayItem, arrayIndex) {
                            item[arrayIndex] = reinstantiateCloneableItems(arrayItem);
                        });

                        return item;

                    // last, it's not clonable but it is an object
                    } else if (angular.isObject(item)) {

                        angular.forEach(item, function (itemValue, itemKey) {
                            item[itemKey] = reinstantiateCloneableItems(itemValue);
                        });

                        return item;

                    // it's something else, primitive etc...
                    } else {

                        return item;
                    }
                };


                /*============ PUBLIC METHODS / PROPERTIES ============*/

                // Set a flag on the object so that we can recursively detect cloneable items
                that._AngularCloneable = true;

                that.clone = function () {

                    // TODO: Why do I need to do this... why can't I just use "that"?
                    var instance = this;

                    // NOTE: angular.copy will preserve the prototypes of nested class instances
                    // The problem is this will not actually invoke "new Class()", so the constructor is never called for nested cloneable class instances
                    // That is in most cases ultimately okay because you will end up with an object in the same state as if the constructor was called
                    // The downside is that if during the constructor an event is fired or external service's state is changed you would lose that during cloning
                    // Another downside is you don't want to hold onto references to any object / function, they should all be new during a clone
                    // If we wanted to add support for nested cloning that would run all class constructors we would need to alter angular.copy or write our own custom copy method

                    // what's so great is angular.copy is already a great and well tested deep copy that covers byteArrays and all kinds of goodness
                    var clonedItem = new instance.constructor(angular.copy(instance));

                    return reinstantiateCloneableItems(clonedItem, true);
                };
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ ENTITY PASSBACK ============*/

            return Cloneable;
        }
    ]);

})();