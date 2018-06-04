package com.sd.projeto3.main;

import com.sd.projeto3.dao.MapaDao;
import com.sd.projeto3.dao.SnapshotDao;
import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.model.MapaDTO;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Willian
 */
public class ThreadSnapshot implements Runnable{

    private static SnapshotDao snapshotDao = new SnapshotDao();
    private static MapaDao mapaDAO = new MapaDao();
    
    @Override
    public void run() {
        while (true) {
            try {
                List<Mapa> logs = new ArrayList<Mapa>();
                Operacoes crud = new Operacoes();
                
                crud.setMapa(new HashMap());
                
                if(snapshotDao.retornaQtdSnapshots() >= 4)
                    snapshotDao.excluirUltimoSnapshot();
              
                snapshotDao.copiarSnapshot();
                logs = mapaDAO.buscarTodos();
                
                for(Mapa m: logs){
                    switch (m.getTipoOperacaoId()) {
                        case 1:
                            crud.salvar(m);
                            break;
                        case 2:
                            crud.editar(m);
                            break;
                        case 3:
                            crud.excluir(m);
                            break;
                        default:
                            break;
                    }
                }
                
                System.out.println("\nSnapshot executado às " + new Date());
                System.out.println("Tamanho do Snapshot: " + crud.getMapa().size() + "\n");
                
                // 5 minutos
                Thread.sleep(100000);
                
            } catch (Exception ex) {
                Logger.getLogger(ThreadSnapshot.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
