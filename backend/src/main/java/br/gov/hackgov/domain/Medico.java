package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "profissional", uniqueConstraints = {
        @UniqueConstraint(name = "uk_profissional_registro_conselho", columnNames = "registro_conselho")
})
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "registro_conselho", nullable = false, length = 30)
    private String registroConselho;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ubs_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profissional_ubs"))
    private Ubs ubs;

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfissionalEspecialidade> especialidades = new LinkedHashSet<>();

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void adicionarEspecialidade(Especialidade especialidade, boolean principal) {
        ProfissionalEspecialidade relacionamento = new ProfissionalEspecialidade();
        relacionamento.setProfissional(this);
        relacionamento.setEspecialidade(especialidade);
        relacionamento.setPrincipal(principal);
        this.especialidades.add(relacionamento);
    }

    public String getEspecialidadePrincipal() {
        return especialidades.stream()
                .sorted(Comparator
                        .comparing(ProfissionalEspecialidade::isPrincipal).reversed()
                        .thenComparing(item -> item.getEspecialidade().getNome()))
                .map(item -> item.getEspecialidade().getNome())
                .findFirst()
                .orElse("");
    }

    public String getEspecialidadesDescricao() {
        return especialidades.stream()
                .sorted(Comparator
                        .comparing(ProfissionalEspecialidade::isPrincipal).reversed()
                        .thenComparing(item -> item.getEspecialidade().getNome()))
                .map(item -> item.getEspecialidade().getNome())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
