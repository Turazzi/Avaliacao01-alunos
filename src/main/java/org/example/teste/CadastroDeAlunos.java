package org.example.teste;

import jakarta.persistence.EntityManager;
import org.example.dao.AlunoDAO;
import org.example.modelo.Aluno;
import org.example.util.JPAUtil;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class CadastroDeAlunos {

    public static void main(String[] args) {

        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        EntityManager em =  JPAUtil.getEntityManager();
        AlunoDAO dao = new AlunoDAO(em);

        try (Scanner sc = new Scanner(System.in)) {
            int option = 0;

            while (option != 6) {
                exibirMenu();
                option = sc.nextInt();
                sc.nextLine();

                switch (option) {
                    case 1:
                        cadastrarAluno(sc, em, dao);
                        break;

                    case 2:
                        removerAluno(dao, sc, em);
                        break;

                    case 3:
                        alterarAluno(dao, sc, em);
                        break;

                    case 4:
                        consultarAluno(dao, sc);
                        break;

                    case 5:
                        exibirTodosOsAlunos(dao);
                        break;

                    case 6:
                        System.out.println("Saindo...");
                        break;

                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            }
        } finally {
            em.close();
        }
    }

    private static void exibirTodosOsAlunos(AlunoDAO dao) {
        List<Aluno> alunos = dao.buscarTodosAlunos();
        if (!alunos.isEmpty()) {
            System.out.println("Exibindo todos os alunos: ");

            for(Aluno a : alunos) {
                System.out.println("\n");
                imprimirAluno(a);
                System.out.println("Media: " + a.getMedia());
                System.out.println("Situação: " + a.getStatus());
            }
        }
        else {
            System.out.println("Não existem alunos cadastrados no banco de dados!");
        }

    }

    private static void consultarAluno(AlunoDAO dao, Scanner sc) {
        System.out.println("\nCONSULTAR ALUNO");
        System.out.println("Digite o nome do aluno: ");

        try {
            List<Aluno> alunosConsultados = dao.buscarPorNome(sc.nextLine());

            if(alunosConsultados.isEmpty()) {
                System.out.println("Aluno não encontrado.");

            }
            else {
                Aluno a = selecionarAluno(alunosConsultados, sc);
                if(a != null) {
                    imprimirAluno(a);
                }
                else {
                    System.out.println("Aluno não encontrado!");
                }
            }
        }
        catch (Exception e) {
            System.out.println("Erro ao concultar o aluno: " + e.getMessage());
        }
    }

    private static void alterarAluno(AlunoDAO dao, Scanner sc, EntityManager em) {
        System.out.println("\nALTERAR ALUNO");
        System.out.println("Digite o nome do aluno: ");

        List<Aluno> alunosParaAlterar = dao.buscarPorNome(sc.nextLine());

        if (alunosParaAlterar.isEmpty()) {
            System.out.println("Aluno não encontrado.");
        }
        else {
            Aluno alunoParaAlterar = selecionarAluno(alunosParaAlterar, sc);
            imprimirAluno(alunoParaAlterar);

            System.out.println("NOVOS DADOS: ");
            persistirAluno(alunoParaAlterar, sc);

            em.getTransaction().begin();
            dao.alterarAluno(alunoParaAlterar);
            em.getTransaction().commit();
            System.out.println("Aluno alterado com sucesso!");

        }
    }

    private static void removerAluno(AlunoDAO dao, Scanner sc, EntityManager em) {
        System.out.println("\nEXCLUIR ALUNO");
        System.out.println("Digite o nome do aluno: ");

        try {
            List<Aluno> alunosParaRemover = dao.buscarPorNome(sc.nextLine());
            if (alunosParaRemover.isEmpty()) {
                System.out.println("Aluno inexistente no banco de dados.");
            }
            else {
                Aluno a = selecionarAluno(alunosParaRemover, sc);

                em.getTransaction().begin();
                dao.remover(a);
                em.getTransaction().commit();
                System.out.println("Aluno removido com sucesso!");
            }
        }
        catch (Exception e) {
            System.out.println("Erro ao remover o aluno: " + e.getMessage());
        }
    }

    private static void cadastrarAluno(Scanner sc, EntityManager em, AlunoDAO dao) {
        Aluno aluno = new Aluno();

        System.out.println("\nCADASTRO DE ALUNO:");

        persistirAluno(aluno, sc);

        em.getTransaction().begin();
        dao.cadastrar(aluno);
        em.getTransaction().commit();
        System.out.println("Aluno cadastrado com sucesso!");
    }

    private static void exibirMenu() {
        System.out.println("*** CADASTRO DE ALUNOS ***\n");
        System.out.println("1 - Cadastrar aluno");
        System.out.println("2 - Excluir aluno");
        System.out.println("3 - Alterar aluno");
        System.out.println("4 - Buscar aluno pelo nome");
        System.out.println("5 - Listar alunos (com status de aprovação!)");
        System.out.println("6 - Sair\n");

        System.out.println("Digite a opção desejada: ");
    }


    private static Aluno selecionarAluno(List<Aluno> alunos, Scanner sc) {
        if (alunos.size() == 1) {
            return alunos.get(0);
        } else {
            System.out.println("Vários alunos encontrados. Selecione o RA do aluno desejado:");
            for (Aluno a : alunos) {
                System.out.println("RA: " + a.getRa() + " - Nome: " + a.getNome());
            }
            String raSelecionado = sc.nextLine();
            return alunos.stream()
                    .filter(a -> a.getRa().equals(raSelecionado))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static void imprimirAluno(Aluno a) {
        System.out.println(("\nDADOS DO ALUNO:"));
        System.out.println("Nome: " + a.getNome());
        System.out.println("Email: " + a.getEmail());
        System.out.println(("RA: " + a.getRa()));
        System.out.println("Notas: " + a.getNota1() + " - " + a.getNota2()
                + " - " + a.getNota3());
    }

    private static void persistirAluno(Aluno aluno, Scanner sc) {
        System.out.println("Digite o nome do aluno: ");
        aluno.setNome(sc.nextLine());

        System.out.println("Digite o RA: ");
        aluno.setRa(sc.nextLine());

        System.out.println("Digite o email: ");
        aluno.setEmail(sc.nextLine());

        System.out.println("Digite a nota 1: ");
        aluno.setNota1(sc.nextBigDecimal());
        sc.nextLine();

        System.out.println("Digite a nota 2: ");
        aluno.setNota2(sc.nextBigDecimal());
        sc.nextLine();

        System.out.println("Digite a nota 3: ");
        aluno.setNota3(sc.nextBigDecimal());
        sc.nextLine();
    }

}
