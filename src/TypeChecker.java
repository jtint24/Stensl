class TypeChecker {
    private static final String allowedTypeCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    /**
     * isValidType
     *
     * Checks if a String represents a valid type
     *
     * @param type The String representing a possible type
     * @return Whether the type string is a valid type
     * */

    public static boolean isValidType(String type) {
        type = type.trim();
        switch (type) {
            case "int", "float", "bool", "string", "char", "void", "any" -> {
                return true;
            }
        }
        if (Interpreter.getClassNames().contains(type)) {

            return true;
        }
        if (isArrayType(type)) {
            return isValidType(unwrapArray(type));
        }
        if (type.startsWith("(") && type.contains(")->")) {
            String retType = type.substring(Interpreter.splitByNakedChar(type, '>')[0].length()+1);
            //System.out.println("retType "+retType);
            if (!isValidType(retType)) {
                return false;
            }
            String rawArglist = Interpreter.splitByNakedChar(type, '>')[0];
            String[] args = Interpreter.splitByNakedChar(rawArglist.substring(1, rawArglist.length()-2),',');
            //System.out.println("args are "+rawArglist.substring(1,rawArglist.length()-2));
            if (type.split("\\)->")[0].substring(1).isBlank()) {
                return true;
            }

            for (String arg : args) {
                if (!isValidType(arg)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * unwrapArray
     *
     * Returns the element type of an array type
     *
     * @param givenType The array type to unwrap
     * */

    public static String unwrapArray(String givenType) {
        if (givenType.startsWith("[") && givenType.endsWith("]")) {
            return givenType.substring(1,givenType.length()-1);
        } else {
            ErrorManager.printError("Cannot unwrap non-array type '"+givenType+"' !","16:2.1");
            return "";
        }
    }

    /**
     * isArrayType
     *
     * Checks whether a type is an array type or not
     *
     * @param givenType The type in question
     * */

    public static boolean isArrayType(String givenType) {
        return givenType.startsWith("[") && givenType.endsWith("]");
    }

    /**
     * isCompatible
     *
     * Checks whether one type can be converted to another
     *
     * @param givenType The type to be converted
     * @param targetType The type that givenType needs to be converted to
     * @return Whether the types are compatible
     * */

    public static boolean isCompatible(String givenType, String targetType) {
        if (givenType.equals(targetType)) {
            return true;
        }
        int gtLength = givenType.length();
        int ttLength = targetType.length();
        int ttIndex = 0;
        for (int gtIndex = 0; gtIndex<gtLength; gtIndex++) {
            //System.out.println(gtIndex+": "+givenType.charAt(gtIndex)+", "+ttIndex+": "+targetType.charAt(ttIndex));
            if (gtIndex<gtLength-2 && ttIndex<ttLength-4) {
                if (givenType.startsWith("int", gtIndex) && targetType.startsWith("float", ttIndex)) {
                    if (gtIndex==gtLength-3) {
                        ttIndex += 5;
                        gtIndex += 3;
                    } else {
                        if (!isValidTypeChar(givenType.charAt(gtIndex+3)) && !isValidTypeChar(targetType.charAt(gtIndex+5))) {
                            ttIndex += 5;
                            gtIndex += 3;
                        } else {
                            return false;
                        }
                    }
                }
            }
            if (gtIndex<gtLength-3 && ttIndex<ttLength-5) {
                if (givenType.startsWith("char", gtIndex) && targetType.startsWith("string", ttIndex) ) {
                    if (gtIndex==gtLength-4) {
                        ttIndex += 6;
                        gtIndex += 4;
                    } else {
                        if (!isValidTypeChar(givenType.charAt(gtIndex+4)) && !isValidTypeChar(targetType.charAt(gtIndex+6))) {
                            ttIndex += 6;
                            gtIndex += 4;
                        } else {
                            return false;
                        }
                    }
                }
            }
            if (ttIndex<ttLength-2) {
                if (targetType.startsWith("any", ttIndex)) {
                    if (ttIndex==ttLength-3) {
                        return true;
                    } else {
                        if (!isValidTypeChar(targetType.charAt(ttIndex+3))) {
                            ttIndex += 6;
                            gtIndex += 4;
                        } else {
                            return false;
                        }
                    }
                }
            }
            if (gtIndex>=gtLength || ttIndex>=ttLength) {
                break;
            }
            if (givenType.charAt(gtIndex) != targetType.charAt(ttIndex)) {
                return false;
            }
            ttIndex++;
        }
        return true;
    }

    /**
     * isValidTypeChar
     *
     * Checks whether a character is allowed to appear in a type
     *
     * @param c The character to check
     * @return Whether the character is allowed to appear in a type
     * */

    private static boolean isValidTypeChar(char c) {
        return allowedTypeCharacters.contains(c+"");
    }
}

//12.7 KB