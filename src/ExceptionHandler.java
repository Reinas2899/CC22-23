public class ExceptionHandler {
    public ExceptionHandler(){}
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean checksFlags(String flag){
        boolean r = false;
        if(flag.equals("Q+R") || flag.equals("Q") || flag.equals("R")) r=true;
        return r;
    }

        public boolean typeExists(String type){
        boolean r=true;
        if(!type.equals(ParameterDB.Tipo.MX.toString()) && !type.equals(ParameterDB.Tipo.NS.toString())
                && !type.equals(ParameterDB.Tipo.A.toString()) && !type.equals(ParameterDB.Tipo.PTR.toString()) && !type.equals(ParameterDB.Tipo.CNAME.toString()) ) r=false;
        return r;
    }
}
