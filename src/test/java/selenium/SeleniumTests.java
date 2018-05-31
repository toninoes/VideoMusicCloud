package selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTests {
		
	public WebDriver driver;
	
	SeleniumTests()
	{
		// Cambiar por directorio donde tengas instalado geckodriver
		// O bien ponerlo en el PATH
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\abreu\\Documents\\PINF\\geckodriver-v0.18.0-win64\\geckodriver.exe");
		this.driver = new FirefoxDriver();		
	}
	
	public static void main(String[] args)
	{
		SeleniumTests test = new SeleniumTests();
		//test.TestRegistrarUsuario();
		//test.TestLogin();
		test.TestSubirVideo();
		//test.TestBuscarVideo();
		//test.TestBuscarVideoFiltrado();
	}
	
	public void TestRegistrarUsuario()
	{
		driver.get("http://localhost:8080/");
		driver.findElement(By.linkText("Regístrate")).click();
		driver.findElement(By.id("nombre")).sendKeys("SeleniumTest");
		driver.findElement(By.id("apellidos")).sendKeys("SeleniumTestA SeleniumTestB");
		driver.findElement(By.id("mail")).sendKeys("SeleniumTest@selenium.com");
		driver.findElement(By.id("password")).sendKeys("SeleniumPassword");
		driver.findElement(By.xpath("//button[contains(.,'Registrar Usuario')]")).click();
	}
	
	public void TestLogin()
	{
		driver.get("http://localhost:8080/");
		driver.findElement(By.id("mail")).sendKeys("SeleniumTest@selenium.com");
		driver.findElement(By.id("password")).sendKeys("SeleniumPassword");
		driver.findElement(By.xpath("//button[contains(.,'Entrar')]")).click();
	}
	
	public void TestSubirVideo()
	{
		TestLogin();
		driver.findElement(By.linkText("Subir Video")).click();
		// Cambiar la ruta por la del video que quieras subar
		driver.findElement(By.id("videobox")).sendKeys("C:\\Users\\abreu\\Videos\\allend\\1_68.mp4");
		driver.findElement(By.id("titulo")).sendKeys("Titulo de Prueba Selenium");
		driver.findElement(By.id("descripcion")).sendKeys("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce congue eu elit eu tincidunt. Nullam ut lectus at tellus convallis varius.");
		Select oSelect = new Select(driver.findElement(By.id("videogeneros")));
		WebDriverWait wait = new WebDriverWait(driver, 7);
		WebElement spinner = driver.findElement(By.xpath("//div[@class='be-loader']"));
		wait.until(ExpectedConditions.invisibilityOf(spinner));
		oSelect.selectByVisibleText("pop");
		driver.findElement(By.id("subirvideo")).submit();
	}
	
	public void TestBuscarVideo()
	{
		TestLogin();
		driver.findElement(By.id("Busqueda")).sendKeys("Selenium");
		driver.findElement(By.id("submit")).click();
	}
	
	public void TestBuscarVideoFiltrado()
	{
		TestLogin();
		Actions actions = new Actions(driver);
		WebElement barra_busqueda = driver.findElement(By.id("Busqueda"));
		actions.moveToElement(barra_busqueda).perform();
		driver.findElement(By.id("Visualizaciones")).click();
		driver.findElement(By.id("Usuario")).click();
		driver.findElement(By.id("Busqueda")).sendKeys("Selenium");
		driver.findElement(By.id("submit")).click();
	}
}