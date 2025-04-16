<template>
    <div class="test-container">
      <h2>Delivery Manager Management Test</h2>
      
      <!-- Crate/Update list -->
      <div class="form-section">
        <h3>Delivery personnel management</h3>
        <input v-model="form.workId" placeholder="Work number" required>
        <div v-if="errors.workId" class="error">{{ errors.workId }}</div>

        <input v-model="form.name" placeholder="Name">
        <div v-if="errors.name" class="error">{{ errors.name }}</div>


        <input  v-model="form.age" type="number" placeholder="Age" min="18" max="65" step="1" @keypress="onlyInteger" >
        <div v-if="errors.age" class="error">{{ errors.age }}</div>
        
        <div class="button-group">
          <button @click="submitDeliver" class="submit-btn">Submit</button>
          <button @click="clearForm" class="clear-btn">Clear All</button>
        </div>
      </div>
  
    
      <div class="action-section">
        <button @click="fetchAllDelivers" class="fetch-btn">Get all delivery personnel</button>
        <input 
          v-model="searchId" 
          placeholder="Enter work number to query"
          @keyup.enter="fetchDeliverById"
        >
      </div>
  
      <!-- Show result -->
      <div class="result-section">
        <h3>Delivery personnel list</h3>
        <div v-if="delivers.length === 0" class="empty-tip">No data available at the moment</div>
        <div v-for="d in delivers" :key="d.workId" class="deliver-item">
          <div class="info">
            <span class="work-id">工号: {{ d.workId }}</span>
            <span>Name: {{ d.name }}</span>
            <span>Age: {{ d.age }}</span>
          </div>
          <div class="actions">
            <button @click="editDeliver(d)" class="edit-btn">Edit</button>
            <button @click="deleteDeliver(d.workId)" class="delete-btn">Delete</button>
          </div>
        </div>
      </div>
  
      <!-- response -->
      <div v-if="responseMessage" class="response-message">
        <pre>{{ responseMessage }}</pre>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios'
  
  export default {
    data() {
      return {  
      errors: { 
        workId: '',
        name: '',
        age: ''
      }, 
        form: {
          workId: '',
          name: '',
          age: null
        },
        searchId: '',
        delivers: [],
        responseMessage: null
      }
    },
    methods: {

      onlyInteger(e) {
        const charCode = e.which ? e.which : e.keyCode;
          if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          e.preventDefault();
        }
      },
      
      validateDeliverForm() { 
      this.errors = { workId: '', name: '', age: '' };
      let isValid = true;

      if (!this.form.workId.trim()) {
        this.errors.workId = 'The work number cannot be empty';
        isValid = false;
      }

      if (!this.form.name.trim()) {
        this.errors.name = 'Name cannot be empty';
        isValid = false;
      } else if (this.form.name.length > 20) {
        this.errors.name = 'The name cannot exceed 20 characters';
        isValid = false;
      }

      if (!this.form.age) {
        this.errors.age = 'Age cannot be empty';
        isValid = false;
      } if (!Number.isInteger(Number(this.form.age))) { 
      this.errors.age = '必须输入整数年龄';
      isValid = false;
      } else if (this.form.age < 18 || this.form.age > 65) {
        this.errors.age = 'The age must be between 18-65 years old and an integer';
        isValid = false;
      }

      return isValid;
    },

      async fetchAllDelivers() {
        try {
          const res = await axios.get('/api/deliver')
          this.delivers = res.data
          this.responseMessage = `Successfully obtained, total ${res.data.length} data!`
        } catch (err) {
          this.handleError(err, 'Failed to obtain the list of delivery personnel')
        }
      },
  
      
      async fetchDeliverById() {
        if (!this.searchId) return
        try {
          const res = await axios.get(`/api/deliver/${this.searchId}`)
          this.delivers = [res.data]
          this.responseMessage = 'query was successful'
        } catch (err) {
          this.handleError(err, 'Failed to query delivery personnel')
        }
      }, 
  
      async submitDeliver() {
        if (!this.validateDeliverForm()) return; 
        try {
          const isUpdate = !!this.form.workId
          const method = isUpdate ? 'put' : 'post'
          const url = isUpdate 
            ? `/api/deliver/${this.form.workId}`
            : '/api/deliver'
  
          const res = await axios[method](url, this.form)
          this.responseMessage = `${isUpdate ? 'Update' : 'Crate'}Successful: ${JSON.stringify(res.data, null, 2)}`
          this.fetchAllDelivers()
          if (!isUpdate) this.clearForm()
        } catch (err) {
          this.handleError(err, 'Fail to submit')
        }
      },
  
      
      async deleteDeliver(workId) {
        if (!confirm(`Are you sure to delete the delivery person with the work number${workId}?`)) return
        try {
          await axios.delete(`/api/deliver/${workId}`)
          this.responseMessage = `Work number ${workId} delete successfully`
          this.fetchAllDelivers()
        } catch (err) {
          this.handleError(err, 'Fail to delete')
        }
      },
  
      
      editDeliver(deliver) {
        this.form = { ...deliver }
      },
  
      
      clearForm() {
        this.form = { workId: '', name: '', age: null }
        this.responseMessage = null
      },
  
      
      handleError(err, defaultMsg) {
        const message = err.response?.data?.message || err.message
        alert(`${defaultMsg}: ${message}`)
        console.error(err)
      }
    }
  }
  </script>
  
  <style scoped>

.error {
  color: #f56c6c;
  font-size: 12px;
  margin: -5px 0 10px 0;
} 

  .test-container {
    max-width: 800px;
    margin: 2rem auto;
    padding: 1rem;
    border: 1px solid #e1e1e1;
    border-radius: 8px;
  }
  
  .form-section {
    margin-bottom: 2rem;
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 6px;
  }
  
  input {
    display: block;
    width: 100%;
    padding: 0.5rem;
    margin: 0.5rem 0;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .button-group {
    margin-top: 1rem;
    display: flex;
    gap: 0.5rem;
  }
  
  button {
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: opacity 0.3s;
  }
  
  button:hover {
    opacity: 0.8;
  }
  
  .submit-btn { background: #67C23A; color: white; }
  .clear-btn { background: #909399; color: white; }
  .fetch-btn { background: #409EFF; color: white; }
  .edit-btn { background: #E6A23C; color: white; }
  .delete-btn { background: #F56C6C; color: white; }
  
  .deliver-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.8rem;
    margin: 0.5rem 0;
    border: 1px solid #eee;
    border-radius: 4px;
  }
  
  .info {
    flex: 1;
    display: flex;
    gap: 1rem;
  }
  
  .work-id {
    font-weight: bold;
    min-width: 120px;
  }
  
  .actions {
    display: flex;
    gap: 0.5rem;
  }
  
  .response-message {
    margin-top: 1rem;
    padding: 1rem;
    background: #f4f4f5;
    border-radius: 4px;
  }
  
  .empty-tip {
    color: #909399;
    text-align: center;
    padding: 1rem;
  }
  </style>