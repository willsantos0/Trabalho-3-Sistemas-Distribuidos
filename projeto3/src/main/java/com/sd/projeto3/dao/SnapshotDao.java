/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd.projeto3.dao;

import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.util.SQLiteConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Willian
 */
public class SnapshotDao implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private static MapaDao mapaDAO = new MapaDao();
    
    
    public int retornarIdUltimoSnapshot() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = SQLiteConnection.connect();

            ps = con.prepareStatement("select id from snapshot order by data desc limit 1");
           
            ResultSet rs = ps.executeQuery();

            return rs.getInt("id");

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar id do snapshot. " + e.getMessage());
        }finally {
            con.close();
        }
    }
    
    
    public boolean criarSnapshot() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = SQLiteConnection.connect();

            ps = con.prepareStatement(
                    "insert into snapshot(data) "
                    + "VALUES (datetime('now', 'localtime'))");
           
            return ps.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new Exception("Erro ao inserir snapshot. " + e.getMessage());
        } finally {
            con.close();
        }

    }
   
    public int retornaQtdSnapshots() throws Exception {
        Connection con = null;

        try {
            con = SQLiteConnection.connect();
            PreparedStatement pstmt = con
                    .prepareStatement("SELECT count(*) as qtd " +
                                "FROM snapshot");
                              
            ResultSet rs = pstmt.executeQuery();
 
            Integer qtd = rs.getInt("qtd");
            
            con.close();
            
            return qtd;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar quantidade de snapshots " + e.getMessage());
        }

    }
    
    public boolean excluirUltimoSnapshot() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = SQLiteConnection.connect();
            ps = con.prepareStatement("delete from snapshot where id = (select id from snapshot order by data asc limit 1)");
            
            if (ps.executeUpdate() > 0) 
                return true;

        } catch (SQLException e) {
            throw new Exception("Erro ao excluir snapshot. " + e.getMessage());
        } finally {
            con.close();
        }
        
        return false;
    }
    
    public void copiarSnapshot(){
        try {
            List<Mapa> mapas = mapaDAO.buscarChaves();
            
            criarSnapshot();
            
            for(Mapa mapa : mapas){
                mapaDAO.salvar(mapa);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(SnapshotDao.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
}
