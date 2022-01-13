package org.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.generation.blogpessoal.model.Usuario;
import org.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L,"Paulo Antunes","link da foto","paulo_antunes@email.com.br","13465278"));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
		.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		assertEquals(requisicao.getBody().getUsuario(), resposta.getBody().getUsuario());
	}

	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
			"Maria da Silva","link da foto","maria_silva@email.com.br","13465278"));
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L,
			"Maria da Silva","link da foto","maria_silva@email.com.br","13465278"));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
			.exchange("/usuarios/cadastrar",HttpMethod.POST, requisicao, Usuario.class);

		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	}
	
	@Test
	@Order(3)
	@DisplayName("Alterar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		
		Optional<Usuario> response = usuarioService.cadastrarUsuario(new Usuario(0L,"Juliana Andrews","link da foto","juliana_andrews@email.com.br","juliana123"));
				
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(
				new Usuario(response.get().getId(),
				"Juliana Andrews Ramos","link da foto","juliana_ramos@email.com.br","juliana123"));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
			.withBasicAuth("juliana_andrews@email.com.br","juliana123")
			.exchange("/usuarios/atualizar",HttpMethod.PUT, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Juliana Andrews Ramos", resposta.getBody().getNome());
		assertEquals("juliana_ramos@email.com.br", resposta.getBody().getUsuario());
	}
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario (0L,
			"Sabrina Sanches","link da foto","sabrina_sanches@email.com.br","sabrina123"));
	
		usuarioService.cadastrarUsuario(new Usuario (0L,
			"Ricardo Marques","link da foto","ricardo_marques@email.com.br","ricardo123"));
		
		ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("tonello", "tonello")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}
