package net.thucydides.core.webdriver.smart;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.thucydides.core.webdriver.smart.findby.FindBy;
import net.thucydides.core.webdriver.smart.findby.How;
import net.thucydides.core.webdriver.smart.findby.SmartBy;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.Annotations;


public class SmartAnnotations extends Annotations {
	
	private Field field;

	public SmartAnnotations(Field field) {
		super(field);
		this.field = field;
	}
	
	private void assertValidAnnotations() {
	    FindBys findBys = field.getAnnotation(FindBys.class);
	    FindBy myFindBy = field.getAnnotation(FindBy.class);
	    if (findBys != null && myFindBy != null) {
	      throw new IllegalArgumentException("If you use a '@FindBys' annotation, "
	          + "you must not also use a '@FindBy' annotation");
	    }
	  }
	
//	@Override
	public By buildBy() {
	    assertValidAnnotations();

	    By ans = null;
	    
	    //default implementation in case if org.openqa.selenium.support.FindBy was used
	    org.openqa.selenium.support.FindBy findBy = field.getAnnotation(org.openqa.selenium.support.FindBy.class);
	    if (ans == null && findBy != null) {
	      ans = super.buildByFromFindBy(findBy);
	    }
	    
	    
	    //my additions to FindBy
	    FindBy myFindBy = field.getAnnotation(FindBy.class);
	    if (ans == null && myFindBy != null) {
	      ans = buildByFromFindBy(myFindBy);
	    }
	    

	    FindBys findBys = field.getAnnotation(FindBys.class);
	    if (ans == null && findBys != null) {
	      ans = buildByFromFindBys(findBys);
	    }
	    

	    if (ans == null) {
	      ans = buildByFromDefault();
	    }

	    if (ans == null) {
	      throw new IllegalArgumentException("Cannot determine how to locate element " + field);
	    }

	    return ans;
    }

	
	protected By buildByFromFindBy(FindBy myFindBy) {
	    assertValidFindBy(myFindBy);

	    By ans = buildByFromShortFindBy(myFindBy);
	    if (ans == null) {
	      ans = buildByFromLongFindBy(myFindBy);
	    }

	    return ans;
	}

	protected By buildByFromLongFindBy(FindBy myFindBy) {
	    How how = myFindBy.how();
	    String using = myFindBy.using();

	    switch (how) {
	      case CLASS_NAME:
	        return By.className(using);

	      case CSS:
	        return By.cssSelector(using);

	      case ID:
	        return By.id(using);

	      case ID_OR_NAME:
	        return new ByIdOrName(using);

	      case LINK_TEXT:
	        return By.linkText(using);

	      case NAME:
	        return By.name(using);

	      case PARTIAL_LINK_TEXT:
	        return By.partialLinkText(using);

	      case TAG_NAME:
	        return By.tagName(using);

	      case XPATH:
	        return By.xpath(using);
	        
	      case SCLOCATOR:
		        return SmartBy.sclocator(using);

	      default:
	        // Note that this shouldn't happen (eg, the above matches all
	        // possible values for the How enum)
	        throw new IllegalArgumentException("Cannot determine how to locate element " + field);
	    }
	}

	protected By buildByFromShortFindBy(FindBy myFindBy) {
	    if (!"".equals(myFindBy.className()))
	      return By.className(myFindBy.className());

	    if (!"".equals(myFindBy.css()))
	      return By.cssSelector(myFindBy.css());

	    if (!"".equals(myFindBy.id()))
	      return By.id(myFindBy.id());

	    if (!"".equals(myFindBy.linkText()))
	      return By.linkText(myFindBy.linkText());

	    if (!"".equals(myFindBy.name()))
	      return By.name(myFindBy.name());

	    if (!"".equals(myFindBy.partialLinkText()))
	      return By.partialLinkText(myFindBy.partialLinkText());

	    if (!"".equals(myFindBy.tagName()))
	      return By.tagName(myFindBy.tagName());

	    if (!"".equals(myFindBy.xpath()))
	      return By.xpath(myFindBy.xpath());
	    
	    if (!"".equals(myFindBy.sclocator()))
		      return SmartBy.sclocator(myFindBy.sclocator());
	    
	    if (!"".equals(myFindBy.jquery()))
		      return SmartBy.jquery(myFindBy.jquery());

	    // Fall through
	    return null;
	}

	private void assertValidFindBy(FindBy findBy) {
	    if (findBy.how() != null) {
	      if (findBy.using() == null) {
	        throw new IllegalArgumentException(
	            "If you set the 'how' property, you must also set 'using'");
	      }
	    }

	    Set<String> finders = new HashSet<String>();
	    if (!"".equals(findBy.using())) finders.add("how: " + findBy.using());
	    if (!"".equals(findBy.className())) finders.add("class name:" + findBy.className());
	    if (!"".equals(findBy.css())) finders.add("css:" + findBy.css());
	    if (!"".equals(findBy.id())) finders.add("id: " + findBy.id());
	    if (!"".equals(findBy.linkText())) finders.add("link text: " + findBy.linkText());
	    if (!"".equals(findBy.name())) finders.add("name: " + findBy.name());
	    if (!"".equals(findBy.partialLinkText()))
	      finders.add("partial link text: " + findBy.partialLinkText());
	    if (!"".equals(findBy.tagName())) finders.add("tag name: " + findBy.tagName());
	    if (!"".equals(findBy.xpath())) finders.add("xpath: " + findBy.xpath());
	    if (!"".equals(findBy.sclocator())) finders.add("scLocator: " + findBy.sclocator());
	    if (!"".equals(findBy.jquery())) finders.add("jquery: " + findBy.jquery());

	    // A zero count is okay: it means to look by name or id.
	    if (finders.size() > 1) {
	      throw new IllegalArgumentException(
	          String.format("You must specify at most one location strategy. Number found: %d (%s)",
	              finders.size(), finders.toString()));
	    }
	}
}