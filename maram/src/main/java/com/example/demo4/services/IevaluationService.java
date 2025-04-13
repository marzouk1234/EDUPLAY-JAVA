/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this temevaluatione file, choose Tools | Temevaluationes
 * and open the temevaluatione in the editor.
 */
package com.example.demo4.services;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author asus
 */
public interface IevaluationService<T> {
    
       public void ajouterevaluation(T t) throws SQLException;
    public void modifierevaluation(T t) throws SQLException;
    public void supprimerevaluation(T t) throws SQLException;
    public List<T> recupererevaluation() throws SQLException;
    
}
