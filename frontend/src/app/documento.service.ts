import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {from, Observable, switchMap} from "rxjs";
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


  uploadFile(file: File): Observable<void> {
    return this.getPresignedUrl().pipe(
      switchMap(presignedUrl => this.uploadToMinIO(presignedUrl, file))
    );
  }

  private getPresignedUrl(): Observable<string> {
    const url = `${this.baseUrl}/presigned-url`;
    return this.httpClient.get<string>(url);
  }

  private uploadToMinIO(presignedUrl: string, file: File): Observable<any> {

    console.log('URL: ', presignedUrl);

    return from(
      fetch(presignedUrl, {
        method: 'PUT',
        body: file,
        headers: {
          'Content-Type': file.type
        }
      }).then(response => {
        if (!response.ok) {
          throw new Error('Falha no upload');
        }
      })
    );
  }



}
