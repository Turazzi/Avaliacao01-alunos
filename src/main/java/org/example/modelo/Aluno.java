package org.example.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "RA", nullable = false)
    private String ra;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nota1")
    private BigDecimal nota1;

    @Column(name = "nota2")
    private BigDecimal nota2;

    @Column(name = "nota3")
    private BigDecimal nota3;

    @Transient
    private BigDecimal media;

    @Transient
    private Status status;

    public Aluno(String nome, String ra, String email, BigDecimal nota1, BigDecimal nota2, BigDecimal nota3) {
        this.nome = nome;
        this.ra = ra;
        this.email = email;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        atualizarMediaEStatus();
    }

    public Aluno() {}

    @PrePersist
    @PreUpdate
    public void atualizarMediaEStatus() {
        setMedia();
        setStatus();
    }

    public void setStatus() {
        if (media == null) {
            setMedia();  // Calcula a média se ainda não estiver definida
        }

        if (media.compareTo(new BigDecimal(4)) < 0) {
            this.status = Status.REPROVADO;
        } else if (media.compareTo(new BigDecimal(4)) >= 0 && media.compareTo(new BigDecimal(6)) < 0) {
            this.status = Status.RECUPERACAO;
        } else {
            this.status = Status.APROVADO;
        }
    }

    private void setMedia() {
        if (nota1 != null && nota2 != null && nota3 != null) {
            BigDecimal soma = nota1.add(nota2).add(nota3);
            this.media = soma.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP);
        } else {
            this.media = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal getMedia() {
        return media;
    }

    public Status getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getNota1() {
        return nota1;
    }

    public void setNota1(BigDecimal nota1) {
        this.nota1 = nota1;
        atualizarMediaEStatus();
    }

    public BigDecimal getNota2() {
        return nota2;
    }

    public void setNota2(BigDecimal nota2) {
        this.nota2 = nota2;
        atualizarMediaEStatus();
    }

    public BigDecimal getNota3() {
        return nota3;
    }

    public void setNota3(BigDecimal nota3) {
        this.nota3 = nota3;
        atualizarMediaEStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id) && Objects.equals(nome, aluno.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }
}
