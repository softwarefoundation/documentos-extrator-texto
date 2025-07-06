import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import {Filtro} from "./filtro.model";

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {


  baseUrl = "http://localhost:8080/documento";

  constructor(private httpClient: HttpClient) {
  }

  buscarPorConteudo(filtro: Filtro): Observable<any> {
    return this.httpClient.post<any>(`${this.baseUrl}/buscar-conteudo`, filtro);
  }

  visualizarFromUUID(uuid: string): Observable<string> {
    const url = `${this.baseUrl}/download/base64/${uuid}`;
    return this.httpClient.get(url, {responseType: 'text'});
  }


  uploadPdf(arquivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', arquivo);
    return this.httpClient.post<string>(`${this.baseUrl}/upload`, formData);
  }

  deleteFromUUID(uuid: string): Observable<Object> {
    return this.httpClient.delete(`${this.baseUrl}/${uuid}`);
  }

}
