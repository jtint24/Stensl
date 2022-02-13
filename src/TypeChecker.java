class TypeChecker {
    private static final String allowedTypeCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    public static String unwrapArray(String givenType) {
        if (givenType.startsWith("[") && givenType.endsWith("]")) {
            return givenType.substring(1,givenType.length()-1);
        } else {
            ErrorManager.printError("Attempt to unwrap non-array type "+givenType+"!");
            return "";
        }
    }
    public static boolean isArrayType(String givenType) {
        return givenType.startsWith("[") && givenType.endsWith("]");
    }
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
                if (givenType.substring(gtIndex,gtIndex+3).equals("int") && targetType.substring(ttIndex, ttIndex+5).equals("float")) {
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
                if (givenType.substring(gtIndex,gtIndex+4).equals("char") && targetType.substring(ttIndex, ttIndex+6).equals("string") ) {
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
                if (targetType.substring(ttIndex,ttIndex+3).equals("any")) {
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
    private static boolean isValidTypeChar(char c) {
        return allowedTypeCharacters.contains(c+"");
    }
}