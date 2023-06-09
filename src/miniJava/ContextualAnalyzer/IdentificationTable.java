package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.Stack;
import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.CodeGenerator.CompilerHint;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class IdentificationTable {
	/*
     * predefined class are in level in example: String Print System --
     * Scopes:
     *   1. class names
     *   2. member names within a class
     *   3. parameter names within a method
     *   4+. local variable names in successively nested scopes within a method
     */
	public static final int PREDEFINED_NAMES_LEVEL = 0;
	public static final int CLASS_NAMES_LEVEL = 1;
	public static final int MEMBER_NAMES_LEVEL = 2;
	public static final int PARAMETER_NAMES_LEVEL = 3;

    public Stack<HashMap<String, Declaration>> table = new Stack<HashMap<String, Declaration>>();
    public HashMap<String, Declaration> classes;
    public HashMap<String, HashMap<String, Declaration>> classFields;
    public HashMap<String, HashMap<String, Declaration>> classMethods;
    
    String classNameContext = null;
    int staticObjectBase = -1;
    
    MethodDecl currentMethod;
    
    static ErrorReporter reporter;
    boolean initialized = false;
    
    //zhou--
    //add one more constructor
      public IdentificationTable() {
      	this(reporter);
      }

    public IdentificationTable(ErrorReporter reporter) {
    	 this.reporter = reporter;
        classes = new HashMap<String, Declaration>();
        classFields = new HashMap<String, HashMap<String, Declaration>>();
        classMethods = new HashMap<String, HashMap<String, Declaration>>();      
        openScope();

      //class _PrintStream { public void println(int n){}; } predefined class print here --zhou
        MethodDeclList PrintStreamMethods = new MethodDeclList();
        MemberDecl printlnField = new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println", null);
        ParameterDeclList printlnParams = new ParameterDeclList();
        ParameterDecl nParameter = new ParameterDecl(new BaseType(TypeKind.INT, null), "n", null);
        printlnParams.add(nParameter);
        MethodDecl printlnMethod = new MethodDecl(printlnField, printlnParams, new StatementList(), null);
        PrintStreamMethods.add(printlnMethod);
        ClassDecl PrintStreamDecl = new ClassDecl("_PrintStream", new FieldDeclList(), PrintStreamMethods, null);
        Identifier PrintStreamIdentifier = new Identifier(new Token(TokenKind.ID, "_PrintStream"),
                new SourcePosition());
        PrintStreamDecl.type = new ClassType(PrintStreamIdentifier, new SourcePosition());
        enter("_PrintStream", PrintStreamDecl);
        printlnMethod.compilerHint = CompilerHint.DEFAULT_PRINTLN;

      //class System { public static _PrintStream out; } predefined System here. --zhou
        Identifier systemIdentifier = new Identifier(new Token(TokenKind.ID, "System"), new SourcePosition());
        FieldDeclList systemFields = new FieldDeclList();
        FieldDecl outField = new FieldDecl(false, true, new ClassType(PrintStreamIdentifier, null), "out", null);
        systemFields.add(outField);
        ClassDecl systemDecl = new ClassDecl("System", systemFields, new MethodDeclList(), null);
        systemDecl.type = new ClassType(systemIdentifier, new SourcePosition());
        enter("System", systemDecl);
        
        //class String { }  predefined class String here  --zhou    
                             
        enter("String",new ClassDecl("String", new FieldDeclList(), new MethodDeclList(), null));
        initialized = true;
    }

    public void openScope() {
        table.push(new HashMap<String, Declaration>());
    }
  //modified -- zhou 
    public void closeScope() {
        table.pop();       
    }
    
    //get current leverl of the scope --zhou 
    public int getScope() {
        return table.size() - 1;
    }
    
    public ClassDecl currentClass() { 
        return (ClassDecl) table.get(1).values().iterator().next();
    }
    

    
    public MethodDecl currentMethod() {
    	return this.currentMethod;
    }
    


   //--get declaration method
    public Declaration retrieve(String id) {
        if (this.classNameContext != null) {
            // we are only retrieving from this class
            String className = this.classNameContext;
            
            if (classMethods.get(className) != null && classMethods.get(className).containsKey(id))
                return classMethods.get(className).get(id);

            if (classFields.get(className) != null && classFields.get(className).containsKey(id))
                return classFields.get(className).get(id);
            
            System.out.println("Retrieval of " + id + " failed in classNameContext " + this.classNameContext);
            return null;
        }
        
        int level = retrieveLevel(id);
        
        if (level == 0 && classes.containsKey(id)) {
            return classes.get(id);
        }
        
        HashMap<String, Declaration> scope = table.get(level);
        if (scope.containsKey(id)) {
            return scope.get(id);
        }
        
        for (String className : classes.keySet()) {
            if (id.equals(className))
                return classes.get(className);

            if (classMethods.get(className) != null && classMethods.get(className).containsKey(id))
                return classMethods.get(className).get(id);

            if (classFields.get(className) != null && classFields.get(className).containsKey(id))
                return classFields.get(className).get(id);
        }
        
        return null;
    }
    
    public Declaration retrieveClass(String className) {
        if (classes.containsKey(className)) {
            return classes.get(className);
        }
        return null;
    }
    
    public Declaration retrieveFieldFromClass(String id, String className) {
        if (classes.containsKey(className)) {
            if (classFields.get(className).containsKey(id)) {
                return classFields.get(className).get(id);
            }
        }
        return null;
    }
    
    public Declaration retrieveMethodFromClass(String id, String className) {
        if (classes.containsKey(className)) {
            if (classMethods.get(className).containsKey(id)) {
                return classMethods.get(className).get(id);
            }
        }
        return null;
    }
    
   
    public int retrieveLevel(String id) {
        for (int level = table.size() - 1; level >= 0; level--) {
            HashMap<String, Declaration> scope = table.get(level);
            if (scope.containsKey(id)) {
                return level;
            }
        }
        
        for (String className : classes.keySet()) {
            if (id.equals(className))
                return 1; // class

            if (classFields.containsKey(className) && classFields.get(className).containsKey(id))
                return 2; // class members
        }

        return 0;
    }
   
    public void enter(String id, Declaration decl) { 
    	boolean check = false;
    	for (int level = 3; level < table.size(); level++) {
            if (table.get(level).containsKey(id)) {
                check = true;
            }
        }
        
        if (table.peek().containsKey(id)) {
            reporter.reportError("*** line " + decl.posn.lineNumber + ": Duplicate declaration of name " + id
                    + " on column " + decl.posn.columnNumber + ".");
        } else if (check) {
            reporter.reportError("*** line " + decl.posn.lineNumber + ": Declaration of variable " + id + " on column "
                    + decl.posn.columnNumber + " hides a variable from too high a scope.");
        } else {
            table.peek().put(id, decl);
        }
    }
    
    public int fieldIndex(String className, String fieldName) {
    	if (classes.containsKey(className)) {
    		ClassDecl classDecl = (ClassDecl) classes.get(className);
    		for (int fieldIndex = 0; fieldIndex < classDecl.fieldDeclList.size(); fieldIndex++) {
    			if (classDecl.fieldDeclList.get(fieldIndex).name.contentEquals(fieldName)) {
    				return fieldIndex;
    			}
    		}
    	}
    	
    	return -1;
    }
}
