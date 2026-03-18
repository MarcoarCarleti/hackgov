package br.gov.hackgov.integration;

import br.gov.hackgov.domain.Ubs;
import br.gov.hackgov.repository.UbsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UbsRepository ubsRepository;

    private Long ubsId;

    @BeforeEach
    void setup() {
        if (ubsRepository.count() == 0) {
            Ubs ubs = new Ubs();
            ubs.setNome("UBS Teste");
            ubs.setEndereco("Rua A");
            ubs.setCidade("Cidade");
            ubs.setEstado("SP");
            ubs.setTelefone("11999999999");
            ubs = ubsRepository.save(ubs);
            ubsId = ubs.getId();
        } else {
            ubsId = ubsRepository.findAll().get(0).getId();
        }
    }

    @Test
    void deveFalharCpfInvalido() throws Exception {
        String payload = """
                {
                  "nome": "Usuario Invalido",
                  "cpf": "12345678900",
                  "cartaoSus": "706105865820555",
                  "email": "invalido@teste.com",
                  "telefone": "11999999999",
                  "senha": "123456",
                  "ubsReferenciaId": %d
                }
                """.formatted(ubsId);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF inválido"));
    }

    @Test
    void deveFalharCadastroDuplicado() throws Exception {
        String primeiro = """
                {
                  "nome": "Usuario 1",
                  "cpf": "39053344705",
                  "cartaoSus": "706105865820123",
                  "email": "u1@teste.com",
                  "telefone": "11999999999",
                  "senha": "123456",
                  "ubsReferenciaId": %d
                }
                """.formatted(ubsId);

        String duplicado = """
                {
                  "nome": "Usuario 2",
                  "cpf": "39053344705",
                  "cartaoSus": "706105865820124",
                  "email": "u2@teste.com",
                  "telefone": "11999999998",
                  "senha": "123456",
                  "ubsReferenciaId": %d
                }
                """.formatted(ubsId);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicado))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF já cadastrado"));
    }
}