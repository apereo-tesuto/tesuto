package org.cccnext.tesuto.util.test

import org.cccnext.tesuto.content.model.DeliveryType
import org.cccnext.tesuto.content.model.ScopedIdentifier
import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Stream

//Useful methods for randomly generating test data
class Generator {

    def rand = new Random()

    int randomInt(int min, int max) {
        if (min == max) {
            return min
        } else {
            return min + rand.nextInt(max - min)
        }
    }

    int randomRangeInt(int min, int max){
        def val = randomInt(min, max)
        return (val > max) ? max : val
    }
    
    double randomDouble(double min, double max) {
        return min + rand.nextDouble() * max
    }

    float randomFloat(float min, float max){
        return min + rand.nextFloat() * max
    }
    String randomId() {
        return UUID.randomUUID().toString()
    }

    String randomString() {
        return randomId() //Good enough for now
    }
    
    String randomString(int min, int max) {
        int length = randomRangeInt(min, max)
        return RandomStringUtils.randomAlphanumeric(length)
    }

    String randomString(int n) {
        return RandomStringUtils.randomAlphanumeric(n)
    }

    String randomWord(int maxlength=100) {
        def word = ''
        for (int i=0; i<randomInt(1,maxlength); ++i) {
            word += randomMember('a' .. 'z')
        }
        word
    }

    String randomEmail() {
        randomWord(10) + '@' + randomWord(10) + '.' + randomWord(10)
    }

    String randomDigits(int length) {
        def digits = ''
        for (int i=0; i<length; ++i) {
            digits += randomMember('0' .. '9')
        }
        digits
    }
    String randomPhone() {
        '('+randomDigits(3) + ')'+randomDigits(3)+'-'+randomDigits(3)
    }

    boolean randomBoolean() {
        return rand.nextInt(2) == 1
    }

    Boolean randomNullableBoolean() {
        return (randomBoolean() ? null : randomBoolean())
    }

    List randomList(int maxLength, Closure closure) {
        return (1..rand.nextInt(maxLength)).collect(closure)
    }
    
    List randomListSetSize(int maxLength, Closure closure) {
        return (1..maxLength).collect(closure)
    }

    Set randomSet(int maxLength, Closure closure) {
        return randomList(maxLength, closure)
    }
    
    Map randomMap(int minLength, int maxLength, Closure closureKey,  Closure closureEntry) {
        return (1..(minLength + rand.nextInt(maxLength))).collectEntries{[closureKey, closureEntry]}
    }

    def randomMember(list) {
        return list[rand.nextInt(list.size())]
    }

    def randomSublist(int n, List list) {
        if (n >= list.size()) {
            return list
        } else {
            def subset = [] as Set
            while (subset.size() < n) {
                subset << randomMember(list)
            }
            return subset.toList()
        }
    }

    //Assumes stream is non-empty
    def randomStreamMember(Stream stream) {
        def array = stream.toArray()
        return array[rand.nextInt(array.length)]
    }

    //pick something from the pool, or generate something new and add that to the pool
    String randomPooledId(pool) {
        return randomPooledObject(pool, { randomId() })
    }

    def randomPooledObject(pool, generator) {
        if (randomBoolean()) {
            return pool.get(rand.nextInt(pool.size()))
        } else {
            def newId = generator()
            pool << newId
            return newId
        }
    }

    Date randomDate() {
        return randomDate(randomBoolean());
    }

    Date randomDateBeforeNow() {
        return randomDate(true);
    }

    //return a date either from before now or after now
    Date randomDate(boolean beforeOrAfter) {
        def now = System.currentTimeMillis()
        def incr = Math.abs(rand.nextLong()) % 10000000 * (beforeOrAfter ? -1 : 1)
        def time = now + incr
        return new Date(time)
    }

    def repeat = { n, closure ->
        while (rand.nextFloat() > 1/n) {
            closure()
        }
    }

    ScopedIdentifier randomScopedIdentifier() {
        new ScopedIdentifier(randomId(), randomId())
    }

    DeliveryType randomDeliveryType() {
        randomBoolean() ? DeliveryType.PAPER : DeliveryType.ONLINE
    }

    String randomPasscode(String prefix, int length) {
        // TODO: Use the PasscodeServiceUtil.createPasscode() to do this.
        char[] PASSCODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTWXYZ23456789".toCharArray()
        return prefix + RandomStringUtils.random(length, PASSCODE_CHARACTERS)
    }

    char randomCB21Code() {
        String cb21Codes = "YABCDEFGH"
        return cb21Codes.charAt(rand.nextInt(cb21Codes.length()))
    }
}
