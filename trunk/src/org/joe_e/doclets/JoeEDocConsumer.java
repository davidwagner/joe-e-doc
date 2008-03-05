package org.joe_e.doclets;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
// import java.util.regex.Pattern;

import org.joe_e.safej.SafeJConsumer;
import org.joe_e.safej.SafeJParse;

import com.sun.javadoc.*;

/**
 * Confirmed that JoeEConsumer is getting initialized properly in JoeEConfiguration
 * 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
 */
public class JoeEDocConsumer implements SafeJConsumer {
    //static final Pattern UNQUALIFY = 
    //    Pattern.compile("[^\\(<> ]*\\.([^<> \\.]*)");

	private HashMap<String, Container> hash;
	private HashMap<String, String> disabledClasses;
	
	public JoeEDocConsumer() {
		hash = new HashMap<String, Container> ();
		disabledClasses = new HashMap<String, String> ();
	}
	
	public JoeEDocConsumer(String sourcepath) {
		this();
		
		if (sourcepath.equals("")) {
			sourcepath = ".";
		}
		try {
		    SafeJParse parser = new SafeJParse(new File(sourcepath), System.err, this);
		} catch (Exception e) {
			System.out.println(e + " in JoeEConsumer.init");
			System.exit(1);
		}
	}
	
	
	/*
	 * Every time we consume a class, we build a Container for all its information
	 * and then we put it into the hash.
	 * 
	 * Motivations: this allows us to consume multiple safej files about differ
	 * classes without mixing up the information
	 */
	public void consumeClass(String className, String classComment, List<String> honoraries,
			List<Member> staticMembers, List<Member> instanceMembers) {
        String dottedClassName = className.replace("$", ".");
		hash.put(dottedClassName, new Container(className, honoraries, staticMembers, instanceMembers, classComment));
	}
	
	public void disabledClass(String className, String classComment) {
		disabledClasses.put(className, classComment);
	}
	
	public boolean isDisabled(String className) {
		return disabledClasses.containsKey(className) || !hash.containsKey(className);
	}

	public String toString() {
		String s = "Printing JoeEConsumer: \n";
		for(String key : hash.keySet()) {
			s += hash.get(key) + "\n";
		}
		for (String k : disabledClasses.keySet()) {
			s += k + " " + hash.get(k) + "\n";
		}
		return s;
	}
	
    public MemberInfo getInfo(ProgramElementDoc member) {
        String className = member.containingClass().qualifiedName();
        if (member instanceof ConstructorDoc) {
            ConstructorDoc ctor = (ConstructorDoc) member;
            String unqualifiedName = 
                ctor.name().substring(ctor.name().lastIndexOf(".") + 1);
            return getInfo(className, unqualifiedName + ctor.flatSignature());
        } else if (member instanceof MethodDoc) {
            MethodDoc method = (MethodDoc) member;
            return getInfo(className, method.name() + method.flatSignature());
        } else if (member instanceof FieldDoc) {
            return getInfo(className, member.name());
        } else { 
            return new MemberInfo(!isDisabled(member.qualifiedName()), null);
        }
    }
    
	public MemberInfo getInfo(String className, String signature)  {
		Container c = hash.get(className);
		if (c == null) {
			return null;
		}
        // standardize varargs to arrays
        signature = signature.replace("...", "[]");
        // standardize to flat signatures
        // signature = UNQUALIFY.matcher(signature).replaceAll("$1");
		MemberInfo m = c.getMember(signature);
		if (m == null) {
			return null;
		}
		return m;
	}
	
    public String dumpInfo(String className) {
        return "" + hash.get(className);
    }
    
	public String getClassDescription(String className) {
		Container c = hash.get(className);
		if (c == null) {
			return null;
		}
		else return c.classDescription;
	}
	
	public String getDisabledComment(String className) {
		return disabledClasses.get(className);
	}
	
	public List<String> getHonoraries(String className) {
		Container c = hash.get(className);
		if (c == null) {
			return null;
		}
		else return c.honoraries;
	}
	
    public class Container {
    	public final String className;
    	public final String classDescription;
    	public List<String> honoraries;
    	public HashMap<String, MemberInfo> members;
    	
    	public Container (String name, List<String> honoraryImps,
    			List<Member> staticMembers, List<Member> instanceMembers, String classComment) {
    		className = name;
    		classDescription = classComment;
    		honoraries = new ArrayList<String> (honoraryImps);
    		members = new HashMap<String, MemberInfo> ();
    		
    		for (Member m : staticMembers) {
    		    members.put(m.identifier, new MemberInfo(m.allowed, m.comment));
            }
            
            for (Member m : instanceMembers) {
                members.put(m.identifier, new MemberInfo(m.allowed, m.comment));
            }
    	}
    	
    	public MemberInfo getMember(String signature) {
    		return members.get(signature);
    	}
    	
    	public String toString() {
    		String s = className;
    		if (classDescription != null) {
    			s += "\n" + classDescription;
    		}
    		s += "\n" + honoraries.toString();
    		s += "\n" + members.toString();
    		return s;
    	}
    }
}
