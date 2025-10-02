/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import jdbc.Conexao;
import model.Cliente;
import model.Venda;



public class VendaDAO {
    private Connection con;


    public VendaDAO() {
       this.con = Conexao.conectar();
    }
    

    public void cadastrarVenda(Venda obj)
    {
        try
        {
            String sql = "INSERT INTO tb_vendas (cliente_id, data_venda, total_venda, observacoes) VALUES (?,?,?,?)"; 
            
            PreparedStatement stmt = con.prepareStatement(sql);
            
            stmt.setInt(1, obj.getCliente().getId());
            stmt.setString(2, obj.getData_venda());
            stmt.setDouble(3, obj.getTotal_venda());
            stmt.setString(4, obj.getObs());
            
            stmt.execute();
            stmt.close();
        }
        catch(Exception erro)
        {
            JOptionPane.showMessageDialog(null, "Erro: " + erro);
        }
    }
    

    public int retornaUltimaVenda()
    {
        try
        {
            int idVenda = 0;
            
            String sql = "SELECT max(id) id FROM tb_vendas";
            
            PreparedStatement ps  = con.prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next())
            {
                Venda p = new Venda();
                
                p.setId(rs.getInt("id"));
                idVenda = p.getId();
            }
            
            return idVenda;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    public List<Venda> listarVendasPorPeriodo(LocalDate data_inicio, LocalDate data_fim)
    {
        try
        {
            List<Venda> lista = new ArrayList<>();

            String sql = "SELECT v.id, date_format(v.data_venda, '%d/%m/%Y') as data_formatada, c.nome, v.total_venda, v.observacoes from  tb_vendas as v inner join tb_clientes as c on (v.cliente_id = c.id) where v.data_venda BETWEEN ? AND ?";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, data_inicio.toString());
            stmt.setString(2, data_fim.toString());

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Venda obj = new Venda();

                Cliente c = new Cliente();

                obj.setId(rs.getInt("v.id"));
                obj.setData_venda(rs.getString("data_formatada"));
                c.setNome(rs.getString("c.nome"));
                obj.setTotal_venda(rs.getDouble("v.total_venda"));
                obj.setObs(rs.getString("v.observacoes"));

                obj.setCliente(c);
                
                lista.add(obj);
            }
            
            return lista;
        }
        catch(SQLException erro)
        {
            JOptionPane.showMessageDialog(null, "Erro: " + erro);
            return null;
        }
    }
    
     //Metodo que calcula total da venda por data
    public double retornaTotalVendaPorData(LocalDate data_venda) {
        try {

            double totalvenda = 0;

            String sql = "select sum(total_venda) as total from tb_vendas where data_venda = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, data_venda.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalvenda = rs.getDouble("total");
            }

            return totalvenda;
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}