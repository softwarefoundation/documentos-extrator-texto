import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import {Filtro} from "./filtro.model";

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {


  baseUrl = "http://localhost:8080/documento/buscar-conteudo";

  constructor(private httpClient: HttpClient) {
  }

  buscarPorConteudo(filtro: Filtro): Observable<any> {
    return this.httpClient.post<any>(this.baseUrl, filtro);
  }

}
