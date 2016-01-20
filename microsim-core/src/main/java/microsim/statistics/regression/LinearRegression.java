package microsim.statistics.regression;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IObjectSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;

public class LinearRegression implements ILinearRegression {
	
	private MultiKeyCoefficientMap map = null;
	
	/**
	 * Linear Regression object.
	 * 
	 * @param map - needs to fulfil two criteria: 1) Map must have a key in the MultiKey that matches the name specified in the RegressionColumnNames enum called Regressor. 2) Map must have a value key that matches the name specified in the RegressionColumnNames enum called Coefficient.	 If loading from an .xls spreadsheet using the ExcelAssistant.loadCoefficientMap(), the Regressor column must be situated to the left of the Coefficient column. 
	 * 
	 * @author Ross Richardson and Michele Sonnessa
	 *  
	 */
	public LinearRegression(MultiKeyCoefficientMap map) {
		this.map = map;

		//Check that map contains the appropriate keys to be a LinearRegression object
		boolean regressorKeyExists = false;
		boolean coefficientValueKeyExists = false;
		boolean regressorKeyInValueKeyByMistake = false;
		boolean coefficientValueKeyInMultiKeyByMistake = false;
		String[] keys = map.getKeysNames();
		String[] valuesNames = map.getValuesNames();
		
		int i = 0;
		while(i < keys.length) {
			if(keys[i].equals(RegressionColumnNames.REGRESSOR.toString())) {
				regressorKeyExists = true;
//				break;
			}
			if(keys[i].equals(RegressionColumnNames.COEFFICIENT.toString())) {
				coefficientValueKeyInMultiKeyByMistake = true;
			}
			i++;
		}
		
		int j = 0;
		while(j < valuesNames.length) {
			if(valuesNames[j].equals(RegressionColumnNames.COEFFICIENT.toString())) {
				coefficientValueKeyExists = true;
//				break;
			}
			if(valuesNames[j].equals(RegressionColumnNames.REGRESSOR.toString())) {
				regressorKeyInValueKeyByMistake = true;
			}
			j++;
		}
		if(!regressorKeyExists) {
			if(regressorKeyInValueKeyByMistake) {
				throw new IllegalArgumentException("The MultiKeyCoefficientMap passed to Linear Regression object has the key named " + RegressionColumnNames.REGRESSOR.toString() + " stored as a value key, whereas it should be a key in the MultiKey.  If loading the MultiKeyCoefficientMap from a .xls file, check the number of key columns and value columns specified in the relevant ExcelAssistant.loadCoefficientMap method call to ensure that the " + RegressionColumnNames.REGRESSOR.toString() + " column is positioned within the key columns, AND to the left of the " + RegressionColumnNames.COEFFICIENT.toString() + " column, which should itself be in the values set. "
						+  "\nThe stack trace is "
						+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
			}
			else throw new IllegalArgumentException("MultiKeyCoefficientMap passed to Linear Regression object does not contain a key in the MultiKey with the required name " + RegressionColumnNames.REGRESSOR.toString() + 
					".  \nThe stack trace is "
					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}
		if(!coefficientValueKeyExists) {
			if(coefficientValueKeyInMultiKeyByMistake) {
				throw new IllegalArgumentException("The MultiKeyCoefficientMap passed to Linear Regression object has a key named " + RegressionColumnNames.COEFFICIENT.toString() + " stored in the MultiKey, whereas it should be a key in the values set.  If loading the MultiKeyCoefficientMap from a .xls file, check the number of key columns and value columns specified in the relevant ExcelAssistant.loadCoefficientMap method call to ensure that the " + RegressionColumnNames.COEFFICIENT.toString() + " column is positioned within the values columns, AND to the right of the " + RegressionColumnNames.REGRESSOR.toString() + " column, which itself should be a key in the map's MultiKey. "
						+  "\nThe stack trace is "
						+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
			}
			else throw new IllegalArgumentException("MultiKeyCoefficientMap passed to Linear Regression object does not contain a key in the values set with the required name " + RegressionColumnNames.COEFFICIENT.toString() + 
					".  \nThe stack trace is "
					+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}

	
	
	//------------------------------------------------------------------------------------
	// Use when only one key (the regressors) exists in map
	//------------------------------------------------------------------------------------

	/**
	 * 
	 * Warning - only use when LinearRegression object contains a MultiKeyCoefficientMap with only one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression, so any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once) will be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect value, so will throw an exception.   
	 * @param values
	 * @return
	 */
	public double getScore(Map<String, Double> values) {
		return computeScore(map, values);
	}
	
	/**
	 * 
	 * Warning - only use when LinearRegression object contains a MultiKeyCoefficientMap with only one key.  This method only looks at the first key of the MultiKeyCoefficientMap field of LinearRegression, so any other keys that are used to distinguish a unique multiKey (i.e. if the first key occurs more than once) will be ignored! If the first key of the multiKey appears more than once, the method would return an incorrect value, so will throw an exception.   
	 * @param values
	 * @return
	 */
	public static double computeScore(MultiKeyCoefficientMap amap, Map<String, Double> values) {
//		try {
			if(amap.getKeysNames().length != 1) {
				throw new IllegalArgumentException("The LinearRegression.computeScore(MultiKeyCoefficientMap amap, Map<String, Double> values) method is designed to be used when the LinearRegression's instance field of type MultiKeyCoefficientMap has only one key in the MultiKey.  Try using other LinearRegression.getScore() methods that cater for more than one key in the MultiKey.");
			}
			double sum = 0.0;
			HashSet<String> regressors = new HashSet<String>();

			for (Object multiKey : amap.keySet()) {
				final String key = (String) ((MultiKey) multiKey).getKey(0);
				if(!regressors.add(key)) {
					throw new IllegalArgumentException("Regressor key " + key + " is not unique!  It is possible that the LinearRegression MultiKeyCoefficientMap containins a MultiKey with more than one key.  The LinearRegression will not return the correct value when LinearRegression.computeScore(MultiKeyCoefficientMap amap, Map<String,Double>) is used with a LinearRegression instance containing a MultiKeyCoefficientMap with more than one key!  Consider using one of the other LinearRegression.getScore() methods instead.");
				}
			}
			
			for (String key : regressors) {
				if (key.contains("@"))
					sum += (Double) (amap.getValue(key) == null ? 0.0 : amap.getValue(key));
				else
					sum += (Double) (amap.getValue(key) == null ? 0.0 : amap.getValue(key)) * (Double) (values.get(key) == null ? 0.0 : values.get(key));
			}
			return sum;
//		} catch (IllegalArgumentException e) {
//			System.err.println(e.getMessage());
//			return 0;
//		} 
	}

	//------------------------------------------------------------------
	// IDoubleSource methods
	//------------------------------------------------------------------
	
	
	public <T extends Enum<T>> double getScore(IDoubleSource iDblSrc, Class<T> enumType) {
		if(map.getKeysNames().length == 1) {
			return computeScore(map, iDblSrc, enumType, true);			//No additional conditioning regression keys used, so no need to check for them
		}
		else {
			return computeScore(map, iDblSrc, enumType);		//Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil status, in order to determine the relevant regression co-efficients.  If time is critical, consider making the underlying agent (the IDoubleSource) also implement the IObjectSource interface, which uses a faster method to retrieve information about the agent instead of reflection.
		}
	}	

	
	/**
	 * Use this method when the underlying agent does not have any additional conditioning regression keys (such as the gender or civil status) to determine the appropriate regression co-efficients, i.e. the regression co-efficients do not depend on any properties of the underlying model. 
	 * Requires that the MultiKeyCoefficientMap only has one entry in the multiKey - that of the name of the regressor variables.  
	 *  
	 * @param coeffMultiMap is a MultiKeyCoefficientMap that has a MultiKey whose first Key is the name of the regressor variable.
	 * @param iDblSrc is an object that implements the IDoubleSource interface, and hence has a method getDoubleValue(enum), where the enum determines the appropriate double value to return.  It must have some fields that match the (case sensitive) name of the keys of coeffMultiMap's MultiKey
	 * @param enumType specifies the enum type that is used in the getDoubleValue(Enum.valueOf(enumType, String)) method of the iDblSrc object.  The String is the name of the enum case, used as a switch to determine the appropriate double value to return
	 * @author Ross Richardson  
	 */
	public static <T extends Enum<T>> double computeScore(MultiKeyCoefficientMap coeffMultiMap, IDoubleSource iDblSrc, Class<T> enumType, boolean singleKeyCoefficients) 
	{
//		System.out.println("singleKeyCoefficients " + singleKeyCoefficients);
//		if(singleKeyCoefficients) {
		if(coeffMultiMap.getKeysNames().length == 1) {			//(double) check that there is only one key entry in the MultiKey of coeffMultiMap
			double sum = 0.;
			for (MapIterator iterator = coeffMultiMap.mapIterator(); iterator.hasNext();) {
				iterator.next();
				
				MultiKey coeffMK = (MultiKey) iterator.getKey();	
				String regressor = coeffMK.getKey(0).toString();							//coeffMK is assumed to only have a single key here
				double covariate = iDblSrc.getDoubleValue(Enum.valueOf(enumType, regressor));		//Gets value of variable with key that matches the regressor string from object implementing IDoubleSource interface
				String columnName = RegressionColumnNames.COEFFICIENT.toString();
				double regCoefficient = ((Number)(coeffMultiMap.getValue(regressor, columnName))).doubleValue();
//				System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
				sum += covariate * regCoefficient;				
			}
			return sum;
		} else {
			throw new IllegalArgumentException("Error - the map of the LinearRegression object has more than one key entry for each multiKey, whereas the LinearRegression.computeScore() method that has been called assumes only one key entry.  Check how computeScore(MultiKeyCoefficientMap, IDoubleSource, boolean) method is being called."
			 + "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}

		
	/**
	 * Uses reflection to obtain information from the iDblSrc object, so it is possibly slow.  For time critical cases, use the other computerScore method that requires 
	 * passing in an object that implements the IObjectSource interface; this has signature:- public static <T extends Enum<T>, U extends Enum<>> double computeScore(MultiKeyCoefficientMap coeffMultiMap, IDoubleSource iDblSrc, Class<T> enumTypeDouble, IObjectSource iObjSrc, Class<> enumTypeObject)
	 * Requires the first column entry of the MultiKeyCoefficientMap (i.e. the first entry of coeffMultiMap's multiKey) to be the name of the regressor variables.  
	 * The names of the other keys of the coeffMultiMap must match the (case sensitive) name of the corresponding fields of the iDblSrc class. 
	 * @param coeffMultiMap is a MultiKeyCoefficientMap that has a MultiKey whose first Key is the name of the regressor variable.  The names of the other keys of the coeffMultiMap must match the (case sensitive) name of the corresponding fields of the iDblSrc class.
	 * @param iDblSrc is an object that implements the IDoubleSource interface, and hence has a method getDoubleValue(enum), where the enum determines the appropriate double value to return.  It must have some fields that match the (case sensitive) name of the keys of coeffMultiMap's MultiKey
	 * @param enumType specifies the enum type that is used in the getDoubleValue(Enum.valueOf(enumType, String)) method of the iDblSrc object.  The String is the name of the enum case, used as a switch to determine the appropriate double value to return
	 * @author Ross Richardson  
	 */
	public static <T extends Enum<T>> double computeScore(MultiKeyCoefficientMap coeffMultiMap, IDoubleSource iDblSrc, Class<T> enumType) 
	{				
//		System.out.println("Reflection method");
		String[] coeffMultiMapKeysNames = coeffMultiMap.getKeysNames();
		try {			
			Map<?, ?> describedData = PropertyUtils.describe(iDblSrc);
			Map<String, String> propertyMap = new HashMap<String, String>();
			
			for(String key : coeffMultiMapKeysNames) {

				if(!key.equals(RegressionColumnNames.REGRESSOR.toString())) {
					if(describedData.containsKey(key)) {
						Object value = describedData.get(key);
						if(value != null)
						{
							if (value.getClass().equals(Double.class)) {
								final Double r = (Double) value;
								propertyMap.put(key, ((Double)(r != null ? r : 0.0)).toString());				
							} else if (value.getClass().equals(Integer.class)) {
								Integer r = (Integer) value;
								propertyMap.put(key, ((Integer)(r != null ? r : 0)).toString());				
							} else if (value.getClass().equals(Boolean.class)) {
								Boolean r = (Boolean) value;
								boolean b = (Boolean)(r != null ? r : false);
								propertyMap.put(key, (b ? "true" : "false"));				
							} else if (value.getClass().equals(String.class)) {
								final String s = (String) value;
								propertyMap.put(key, s);	
							} else if (value.getClass().equals(Long.class)) {
								final Long r = (Long) value;
								propertyMap.put(key, ((Long)(r != null ? r : 0L)).toString());				
							} else if (value.getClass().isEnum()) {
								final String e = value.toString();
								propertyMap.put(key, e);								
							} else if (value.getClass().equals(Float.class)) {
								final Float r = (Float) value;
								propertyMap.put(key, ((Float)(r != null ? r : 0.0f)).toString());				
							} 
						}
					}
					else throw new NoSuchFieldException("Error in Regression: Could not find LinearRegression.map key named \'" + key + "\' among the fields of the iDoubleSource argument to computeScore(MultiKeyCoefficientMap, IDoubleSource, Class<T>).  Check the character cases of \'" + key + "\' match, if the field exists in object implementing iDoubleSource, to ensure they match.  If they do match, check that a getter method for the field exists following the Java Beans convention.");
				}
				
				
			}			
				
			double sum = 0.;
			int regressorColumnIndex = -1;
			for (MapIterator iterator = coeffMultiMap.mapIterator(); iterator.hasNext();) {
				iterator.next();
				
				MultiKey coeffMK = (MultiKey) iterator.getKey();
				boolean coeffMKapplicableForIDblSrc = true;
				int i = 0;	
				while(i < coeffMultiMapKeysNames.length) {					
					if(!coeffMK.getKey(i).toString().equals(propertyMap.get(coeffMultiMapKeysNames[i]))) {
						if(coeffMultiMapKeysNames[i].equals(RegressionColumnNames.REGRESSOR.toString())) {
							regressorColumnIndex = i;
						}
						else {
							coeffMKapplicableForIDblSrc = false;		
							break;
						}
					}
					i++;
				}

				if(coeffMKapplicableForIDblSrc == true) {
					String regressor = ((String) ((MultiKey) coeffMK).getKey(regressorColumnIndex));
					double covariate = iDblSrc.getDoubleValue(Enum.valueOf(enumType, regressor));		//Gets value of variable with key that matches the regressor string from object implementing IDoubleSource interface
					if(coeffMultiMap.getValuesNames().length > 1) {			//Case when coeffMultiMap has more than one value column
//						Object[] fullKeys = new Object[coeffMK.getKeys().length+1];
//						for(int i1 = 0; i1 < coeffMK.getKeys().length; i1++) {
//							fullKeys[i1] = coeffMK.getKey(i1);
//						}
						String columnName = RegressionColumnNames.COEFFICIENT.toString();
//						fullKeys[coeffMK.getKeys().length] = columnName;

//						double regCoefficient = ((Number)coeffMultiMap.getValue(fullKeys)).doubleValue();
						double regCoefficient = ((Number)coeffMultiMap.getValue(coeffMK, columnName)).doubleValue();
						sum += covariate * regCoefficient;
//						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
					else {							//Case when coeffMultiMap only has one value column
						double regCoefficient = ((Number)coeffMultiMap.get(coeffMK)).doubleValue();		
						sum += covariate * regCoefficient;
//						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
				}
					
			}
			return sum;
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (NoSuchFieldException e) {
			System.err.println(e.getMessage());
			return 0;
		}
	}
	
	/** 
	 * To be used when the agent Object has member fields that correspond by name to 
	 * all the regressors of the Regression's MultiKeyCoefficientMap map.  The agent 
	 * must also have fields that specify any additional conditioning keys e.g. gender
	 * or civil status, of the regression map's MultiKeys.
	 * 
	 * @param agent is the object whose fields must match by name, the regressors and 
	 *  additional conditioning keys of the Regression map's MultiKeys.  
	 * @author Ross Richardson  
	 */
	public double getScore(Object agent) {
		return computeScore(map, agent);
	}

	/** 
	 * To be used when the agent Object has member fields that correspond by name to 
	 * all the regressors of the Regression's MultiKeyCoefficientMap map.  The agent 
	 * must also have fields that specify any additional conditioning keys e.g. gender
	 * or civil status, of the regression map's MultiKeys.
	 * 
	 * @param agent is the object whose fields must match by name, the regressors and 
	 *  additional conditioning keys of the Regression map's MultiKeys.  
	 * @author Ross Richardson  
	 */
	public static double computeScore(MultiKeyCoefficientMap coeffMultiMap, Object agent) {		
		try {			
			final Map<String, String> propertyMap = extractMapNumbersBooleansEnumsAndStrings(agent);	//Extract fields of agent as strings
			final Map<String, Double> valueMap = extractMapNumbersAndBooleans(agent);		//Extract numerical fields of agent
			
			String[] coeffMultiMapKeysNames = coeffMultiMap.getKeysNames();
			int iMax = coeffMultiMapKeysNames.length;
			String[] attributes = new String[iMax];
			int regressorColumnIndex = -1;
			for(int i = 0; i < iMax; i++) {
				String key = coeffMultiMapKeysNames[i];
				if(key.equals(RegressionColumnNames.REGRESSOR.toString())) {
					regressorColumnIndex = i;
					attributes[i] = "";		//So we don't get null pointer exceptions later
				}
				else {
					if(propertyMap.containsKey(key)) {
						attributes[i] = propertyMap.get(key);
					}
					else {
						throw new IllegalArgumentException("The " + agent.getClass().getCanonicalName() + " object does not contain a member field called '" + key + "' to match the conditioning key of the Regression object.  If you believe this should not be the case, consider checking the spelling and case (lower / upper) of the member field in the " + agent.getClass().getSimpleName() + " object." 
								+  "\nThe stack trace is "
								+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
					};
				}
			}
			
			double score = 0.;
			for (MapIterator iterator = coeffMultiMap.mapIterator(); iterator.hasNext();) {
				iterator.next();
				MultiKey coeffMK = (MultiKey) iterator.getKey();
				boolean coeffMKapplicable = true;
				int i = 0;	
				while(i < iMax) {
					if(!coeffMK.getKey(i).toString().equals(attributes[i])) {						
						if(i != regressorColumnIndex) {
							coeffMKapplicable = false;		
							break;
						}
					}
					i++;
				}
				if(coeffMKapplicable == true) {
					String regressor = ((String) ((MultiKey) coeffMK).getKey(regressorColumnIndex));
					double covariate = Double.MIN_VALUE;
					if(valueMap.containsKey(regressor)) {
						covariate = valueMap.get(regressor);		//Gets value of variable with key that matches the regressor string from object implementing IDoubleSource interface	
					}
					else {
						throw new IllegalArgumentException(agent.getClass().getCanonicalName() + " object does not contain a field called " + regressor + ".  If there is supposed to be such a field, check the upper/lower case of the regressor specified in the MultiKeyCoefficientMap of the Regression object to ensure that it matches the name of the " + agent.getClass().getSimpleName() + "object's field."
								+  "\nThe stack trace is "
								+ "\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
					}
												
					if(coeffMultiMap.getValuesNames().length > 1) {			//Case when coeffMultiMap has more than one value column
						Object[] fullKeys = new Object[iMax+1];
						for(int i1 = 0; i1 < iMax; i1++) {
							fullKeys[i1] = coeffMK.getKey(i1);
						}
						String columnName = RegressionColumnNames.COEFFICIENT.toString();
						fullKeys[iMax] = columnName;

						double regCoefficient = ((Number)coeffMultiMap.getValue(fullKeys)).doubleValue();
						score += covariate * regCoefficient;
						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
					else {							//Case when coeffMultiMap only has one value column
						double regCoefficient = ((Number)coeffMultiMap.get(coeffMK)).doubleValue();		
						score += covariate * regCoefficient;
						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
				}
				
			}
				
			return score;
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
			return 0;
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			return 0;
//		} catch (NoSuchFieldException e) {
//			System.err.println(e.getMessage());
//			return 0;
		}
	}
	
	public static double multiplyCoeffsWithValues(Map<String, Double> regCoeffMap, Map<String, Double> valueMap) {
		
		double sum = 0.0;
		try {
			for (String key : regCoeffMap.keySet()) {
				if (key.contains("@"))
					sum += (Double) (regCoeffMap.get(key) == null ? 0.0 : regCoeffMap.get(key));
				else
					sum += (Double) (regCoeffMap.get(key) == null ? 0.0 : regCoeffMap.get(key)) * (Double) (valueMap.get(key) == null ? 0.0 : valueMap.get(key));
			}
			return sum;

		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return 0;
		} 
	}

	public <T extends Enum<T>, U extends Enum<U>> double getScore(IDoubleSource iDblSrc, Class<T> enumTypeDouble, IObjectSource iObjSrc, Class<U> enumTypeObject) {
		return computeScore(map, iDblSrc, enumTypeDouble, iObjSrc, enumTypeObject);
	}	
	
	/**
	 * Requires the implementation of the IObjectSource to ascertain whether any additional conditioning regression keys are used (e.g. whether the underlying agent is female, married etc., where the regression co-efficients are conditioned on additional keys of gender and civil status, for example).
	 * If the underlying agent does not implement IObjectSource but does have additional conditioning regression keys, use the computeScore method (that uses reflection, so is slower) with signature:- public static <T extends Enum<T>> double computeScore(MultiKeyCoefficientMap coeffMultiMap, IDoubleSource iDblSrc, Class<T> enumType)
	 * If the underlying agent does not have additional conditioning regression keys, use the computeScore method with signature:-     
	 * Requires the MultiKeyCoefficientMap coeffMultiMap to have a key in its multiKey that corresponds to the name of the regressor variables.  
	 * The names of the other keys of the coeffMultiMap must match the (case sensitive) name of the corresponding fields of the iDblSrc class. 
	 * @param coeffMultiMap is a MultiKeyCoefficientMap that has a MultiKey containing the name of the regressor variable.  The names of the other keys of the coeffMultiMap must match the (case sensitive) name of the corresponding fields of the iDblSrc class.
	 * @param iDblSrc is an object that implements the IDoubleSource interface (e.g. the underlying agent whose properties are the covariates), and hence has a method getDoubleValue(enum), where the enum determines the appropriate double value to return.  It must have some fields that match the (case sensitive) name of the first key entry of the coeffMultiMap's MultiKey
	 * @param enumTypeDouble specifies the enum type that is used in the getDoubleValue(Enum.valueOf(enumType, String)) method of the iDblSrc object.  The String is the name of the enum case, used as a switch to determine the appropriate double value to return
	 * @param iObjSrc is an object that implements the IObjectSource interface (e.g. the underlying agent whose properties are the covariates), and hence has a method getObjectValue(enum), where the enum determines the appropriate double value to return.  It must have some fields that match the (case sensitive) name of the conditioning regression key entries of coeffMultiMap's MultiKey (not the first key entry, which is reserved for the regressor name)
	 * @param enumTypeObject specifies the enum type that is used in the getObjectValue(Enum.valueOf(enumType, String)) method of the iObjSrc object.  The String is the name of the enum case, used as a switch to determine the appropriate object value to return

	 * @author Ross Richardson  
	 */
	public static <T extends Enum<T>, U extends Enum<U>> double computeScore(MultiKeyCoefficientMap coeffMultiMap, IDoubleSource iDblSrc, Class<T> enumTypeDouble, IObjectSource iObjSrc, Class<U> enumTypeObject) 
	{				
			double sum = 0.;
			int regressorColumnIndex = -1;
			String[] coeffMultiMapKeysNames = coeffMultiMap.getKeysNames();
			for (MapIterator iterator = coeffMultiMap.mapIterator(); iterator.hasNext();) {
				iterator.next();
				
				MultiKey coeffMK = (MultiKey) iterator.getKey();
				boolean coeffMKapplicableForIDblSrc = true;
				int i = 0;	
				while(i < coeffMultiMapKeysNames.length) {
					if(coeffMultiMapKeysNames[i].equals(RegressionColumnNames.REGRESSOR.toString())) {
						regressorColumnIndex = i;
//						System.out.println("regressor column index is " + regressorColumnIndex);
					}
					else {
						if(!coeffMK.getKey(i).toString().equals(iObjSrc.getObjectValue(Enum.valueOf(enumTypeObject, coeffMultiMapKeysNames[i])).toString())) {
							coeffMKapplicableForIDblSrc = false;		
							break;
						}						
					}

					i++;
				}
				if(coeffMKapplicableForIDblSrc == true) {
					String regressor = ((String) ((MultiKey) coeffMK).getKey(regressorColumnIndex));
					double covariate = iDblSrc.getDoubleValue(Enum.valueOf(enumTypeDouble, regressor));		//Gets value of variable with key that matches the regressor string from object implementing IDoubleSource interface
					if(coeffMultiMap.getValuesNames().length > 1) {			//Case when coeffMultiMap has more than one value column
						Object[] fullKeys = new Object[coeffMK.getKeys().length+1];
						for(int i1 = 0; i1 < coeffMK.getKeys().length; i1++) {
							fullKeys[i1] = coeffMK.getKey(i1);
						}
						String columnName = RegressionColumnNames.COEFFICIENT.toString();
						fullKeys[coeffMK.getKeys().length] = columnName;

						double regCoefficient = ((Number)coeffMultiMap.getValue(fullKeys)).doubleValue();
						sum += covariate * regCoefficient;
//						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
					else {							//Case when coeffMultiMap only has one value column
						double regCoefficient = ((Number)coeffMultiMap.get(coeffMK)).doubleValue();		
						sum += covariate * regCoefficient;
//						System.out.println("regressor " + regressor + ", " + "covariate " + covariate + ", " + " regCoefficient " + regCoefficient);
					}
				}
					
			}
			return sum;
	}
	
	private static Map<String, Double> extractMapNumbersAndBooleans(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Double> resultMap = new HashMap<String, Double>();
		
		Map<?, ?> describedData = PropertyUtils.describe(object);
		
		for (Iterator<?> iterator = describedData.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = describedData.get(key);
			if(value != null)
			{
				if (value.getClass().equals(Double.class)) {
					final Double r = (Double) value;
					resultMap.put(key, (r != null ? r : 0.0));				
				} else if (value.getClass().equals(Float.class)) {
					final Float r = (Float) value;
					resultMap.put(key, ((Float)(r != null ? r : 0.0f)).doubleValue());				
				} else if (value.getClass().equals(Long.class)) {
					final Long r = (Long) value;
					resultMap.put(key, ((Long)(r != null ? r : 0L)).doubleValue());				
				} else if (value.getClass().equals(Integer.class)) {
					Integer r = (Integer) value;
					resultMap.put(key, ((Integer)(r != null ? r : 0)).doubleValue());				
				} else if (value.getClass().equals(Boolean.class)) {
					Boolean r = (Boolean) value;
					boolean b = (Boolean)(r != null ? r : false);
					resultMap.put(key, (b ? 1.0 : 0.0));				
				}
			} 
		}
		
		return resultMap;
	}
	
	private static Map<String, String> extractMapNumbersBooleansEnumsAndStrings(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		Map<?, ?> describedData = PropertyUtils.describe(object);
		
		for (Iterator<?> iterator = describedData.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = describedData.get(key);
			if(value != null)
			{
				if (value.getClass().equals(Double.class)) {
					final Double r = (Double) value;
					resultMap.put(key, ((Double)(r != null ? r : 0.0)).toString());				
				} else if (value.getClass().equals(Integer.class)) {
					Integer r = (Integer) value;
					resultMap.put(key, ((Integer)(r != null ? r : 0)).toString());				
				} else if (value.getClass().equals(Boolean.class)) {
					Boolean r = (Boolean) value;
					boolean b = (Boolean)(r != null ? r : false);
					resultMap.put(key, (b ? "true" : "false"));				
				} else if (value.getClass().equals(String.class)) {
					final String s = (String) value;
					resultMap.put(key, s);	
				} else if (value.getClass().equals(Long.class)) {
					final Long r = (Long) value;
					resultMap.put(key, ((Long)(r != null ? r : 0L)).toString());				
				} else if (value.getClass().isEnum()) {
					final String e = value.toString();
					resultMap.put(key, e);								
				} else if (value.getClass().equals(Float.class)) {
					final Float r = (Float) value;
					resultMap.put(key, ((Float)(r != null ? r : 0.0f)).toString());				
				} 
			} 
		}
		
		return resultMap;
	}
	
}
