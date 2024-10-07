package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.modelo.Aluno;

import java.util.List;

public class AlunoDAO {

    private EntityManager em;

    public AlunoDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar (Aluno aluno) {
        this.em.persist(aluno);
    }

    public List<Aluno> buscarPorNome(String nome) throws NoResultException {
        String jpql = "SELECT a FROM Aluno a WHERE a.nome = :nome";
        return em.createQuery(jpql, Aluno.class).setParameter("nome", nome).getResultList();
    }

    public List<Aluno> buscarTodosAlunos() {
        List<Aluno> alunos = em.createQuery("SELECT a FROM Aluno a", Aluno.class).getResultList();

        for(Aluno a : alunos) {
            a.atualizarMediaEStatus();
        }
        return alunos;
    }

    public void alterarAluno(Aluno aluno) {
        this.em.merge(aluno);
    }

    public void remover (Aluno aluno) {
        this.em.remove(aluno);
    }
}
